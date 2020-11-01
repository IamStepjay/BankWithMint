package com.bankwith.mint.dto.response;

import lombok.Data;

@Data
public class PlainResponse {
	
	private Number number;
	
	private String scheme;
	
	private String type;
	
	private String brand;
	
	private String prepaid;
	
	private Country country;
	
	private Bank bank ;

}
