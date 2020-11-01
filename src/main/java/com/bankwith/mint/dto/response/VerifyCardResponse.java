package com.bankwith.mint.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VerifyCardResponse {
	
	private String success;
	
	@JsonProperty("payload")
	private VerifyCardPayload verifyCardPayload;

}
