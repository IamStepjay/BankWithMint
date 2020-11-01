package com.bankwith.mint.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankwith.mint.dto.request.Payload;
import com.bankwith.mint.dto.response.HitCountResponse;
import com.bankwith.mint.dto.response.VerifyCardResponse;
import com.bankwith.mint.service.BankWithMintService;


@Validated
@CrossOrigin
@RestController
@RequestMapping("/card-scheme")
public class TransactionController {
	
	private final BankWithMintService bankWithMintService;

    public TransactionController(BankWithMintService bankWithMintService) {
        this.bankWithMintService = bankWithMintService;
    }

    @GetMapping("/verify")
    public VerifyCardResponse verifyCard(@RequestParam("card") String card) {
    	return bankWithMintService.verifyCard(card);
    }
    
    @GetMapping("/status")
    public HitCountResponse hitCount(@RequestParam("start") String start, @RequestParam("limit") String limit) {
    	return bankWithMintService.hitCount(start, limit);
    }
    
    @PostMapping("/publish")
    public void sendMessageToKafkaTopic(Payload payload) {
    	bankWithMintService.sendMessageWithCallback(payload);
    }

}
