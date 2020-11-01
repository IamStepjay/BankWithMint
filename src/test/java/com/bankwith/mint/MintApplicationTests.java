package com.bankwith.mint;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.bankwith.mint.dto.response.VerifyCardPayload;
import com.bankwith.mint.dto.response.VerifyCardResponse;
import com.bankwith.mint.service.BankWithMintService;
import com.google.gson.Gson;

@SpringBootTest
@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
public class MintApplicationTests {

	private MockMvc mockMvc;

	@MockBean
	private BankWithMintService bankWithMintService;

	String authStr = "Test:test";
	String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
	String headerValue = "Basic " + base64Creds;

	@BeforeEach
	public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(documentationConfiguration(restDocumentation)).build();
	}
	
	@Test
	public void verifyCard() throws Exception {
//		String card = "45717360";
		VerifyCardResponse response = new VerifyCardResponse();
		response.setSuccess("true");
		VerifyCardPayload verifyCardPayload = new VerifyCardPayload();
		verifyCardPayload.setScheme("visa");
		verifyCardPayload.setType("debit");
		verifyCardPayload.setBank("Jyske Bank");
		response.setVerifyCardPayload(verifyCardPayload);
		Mockito.when(bankWithMintService.verifyCard(Mockito.any())).thenReturn(response);
		this.mockMvc
				.perform(RestDocumentationRequestBuilders
						.get("/card-scheme/verify/?card=45717360").header("Authorization", headerValue)
						.content(new Gson().toJson(null)).accept("*/*").contentType(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andDo(document("mint/verifyCard", Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
						Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
						PayloadDocumentation.requestFields(
								PayloadDocumentation.fieldWithPath("card").description("Customer's Card's first 6 or 8 digits of a payment card number (credit cards, debit cards, etc.) are known as the Issuer Identification Numbers (IIN), previously known as Bank Identification Number (BIN). These identify the institution that issued the card to the card holder")
								)))
				.andExpect(status().isOk());
	}
	
}
