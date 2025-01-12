package com.skcorp.skbank.card_service.controllers;

import com.skcorp.skbank.card_service.client.api.SkBankCardsApi;
import com.skcorp.skbank.card_service.client.models.CardRequest;
import com.skcorp.skbank.card_service.client.models.CardResponse;
import com.skcorp.skbank.card_service.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardController implements SkBankCardsApi {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @Override
    public ResponseEntity<CardResponse> addNewCard(CardRequest cardRequest) {

        CardResponse response = cardService.addNewCard(cardRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
