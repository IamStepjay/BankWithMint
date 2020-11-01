package com.bankwith.mint.dto.response;

import lombok.Data;

@Data
public class VerifyCardPayload {
	
	private String scheme;
	
	private String type;
	
	private String bank;

}
