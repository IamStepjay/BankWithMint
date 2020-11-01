package com.bankwith.mint.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.bankwith.mint.dto.request.Payload;
import com.bankwith.mint.dto.response.HitCountResponse;
import com.bankwith.mint.dto.response.PlainResponse;
import com.bankwith.mint.dto.response.VerifyCardPayload;
import com.bankwith.mint.dto.response.VerifyCardResponse;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BankWithMintService {

	private final RestTemplate restTemplate;

	private final Gson gson;

	private String enquiryUrl;

	private static final String topic = "com.ng.vela.even.card_verified";

	private KafkaTemplate<String, Payload> kafkaTemplate;

	public BankWithMintService(RestTemplate restTemplate, Gson gson,
			@Value("${bankwithmint.enquiry-url}") String enquiryUrl, KafkaTemplate<String, Payload> kafkaTemplate) {
		this.restTemplate = restTemplate;
		this.gson = gson;
		this.enquiryUrl = enquiryUrl;
		this.kafkaTemplate = kafkaTemplate;
	}

	public VerifyCardResponse verifyCard(String card) {

		String finalUrl = enquiryUrl + card;

		String response = executeRequest(finalUrl, HttpMethod.GET);

		PlainResponse plainResponse = gson.fromJson(response, PlainResponse.class);

		VerifyCardResponse verifyCardResponse = new VerifyCardResponse();

		if (response.contains("true")) {
			VerifyCardPayload verifyCardPayload = new VerifyCardPayload();
			verifyCardPayload.setScheme(plainResponse.getScheme());
			verifyCardPayload.setType(plainResponse.getType());
			verifyCardPayload.setBank(plainResponse.getBank().getName());

			verifyCardResponse.setSuccess(plainResponse.getNumber().getLuhn());
			verifyCardResponse.setVerifyCardPayload(verifyCardPayload);

			return verifyCardResponse;
		}
		return null;

	}
	
	public HitCountResponse hitCount(String start, String limit) {

		String finalUrl = enquiryUrl + start + "/" + limit;

		String response = executeRequest(finalUrl, HttpMethod.GET);

		PlainResponse plainResponse = gson.fromJson(response, PlainResponse.class);

		HitCountResponse hitCountResponse = new HitCountResponse();

		if (response.contains("true")) {
			VerifyCardPayload verifyCardPayload = new VerifyCardPayload();
			verifyCardPayload.setScheme(plainResponse.getScheme());
			verifyCardPayload.setType(plainResponse.getType());
			verifyCardPayload.setBank(plainResponse.getBank().getName());

			hitCountResponse.setSuccess(plainResponse.getNumber().getLuhn());
			hitCountResponse.setStart(start);
			hitCountResponse.setLimit(limit);
			hitCountResponse.setSize(plainResponse.getNumber().getLength());
			hitCountResponse.setVerifyCardPayload(verifyCardPayload);

			return hitCountResponse;
		}
		return null;

	}

	private String executeRequest(String url, HttpMethod httpMethod) {

		log.info("The Final URL::::::::::" + url);
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, httpMethod, null, String.class);
			log.info("BankWithMint api response {}", response.getBody());
			return response.getBody();
		} catch (HttpClientErrorException e) {
			log.info("Error response {}", e.getResponseBodyAsString());
			return e.getResponseBodyAsString();
		} catch (RestClientException e) {
			log.info("RestClientException {}", e.getMessage());
		}

		return null;
	}

	public void sendMessageWithCallback(Payload payload) {
		ListenableFuture<SendResult<String, Payload>> future = kafkaTemplate.send(topic, payload);

		future.addCallback(null, new ListenableFutureCallback<SendResult<String, Payload>>() {
			@Override
			public void onSuccess(SendResult<String, Payload> result) {
				log.info("Message [{}] delivered with offset {}", payload, result.getRecordMetadata().offset());
			}

			@Override
			public void onFailure(Throwable ex) {
				log.warn("Unable to deliver message [{}]. {}", payload, ex.getMessage());
			}
		});
	}

}
