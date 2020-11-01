package com.bankwith.mint.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class HitCountResponse {
	
	private String success;

	private String start;

	private String limit;

	private String size;
	
	@JsonProperty("payload")
	private VerifyCardPayload verifyCardPayload;
	
}
