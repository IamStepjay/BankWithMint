package com.bankwith.mint.controller;

import static org.junit.Assert.assertTrue;

import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.bankwith.mint.MintApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MintApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MintControllerTest {

	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();

	String authStr = "Test:test";
	String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
	String headerValue = "Basic " + base64Creds;

	HttpHeaders headers = new HttpHeaders();

	@Test
	public void VerifyCard() {

		headers.add("Authorization", headerValue);

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/card-scheme/verify/?card=45717360"),
				HttpMethod.GET, entity, String.class);

		log.info("Verify Card Test RESPONSE " + response.getBody().toString());

		String actual = response.getBody().toString();

		assertTrue(actual.contains("scheme"));

	}

	@Test
	public void HitCount() {

		headers.add("Authorization", headerValue);

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/card-scheme/status?start=1&limit=3"), HttpMethod.GET, entity, String.class);

		log.info("Hit Count Test RESPONSE " + response.getBody().toString());

		String actual = response.getBody().toString();

		assertTrue(actual.contains("luhn"));

	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}

}
