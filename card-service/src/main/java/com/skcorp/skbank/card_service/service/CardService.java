package com.skcorp.skbank.card_service.service;

import com.skcorp.skbank.card_service.client.models.CardRequest;
import com.skcorp.skbank.card_service.client.models.CardResponse;

public interface CardService {
    CardResponse addNewCard(CardRequest cardRequest);
}
