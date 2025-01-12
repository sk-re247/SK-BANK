package com.skcorp.skbank.card_service.service.impl;

import com.skcorp.skbank.card_service.client.models.CardRequest;
import com.skcorp.skbank.card_service.client.models.CardResponse;
import com.skcorp.skbank.card_service.client.models.CardTypeEnum;
import com.skcorp.skbank.card_service.common.GlobalServiceHelper;
import com.skcorp.skbank.card_service.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardServiceImpl implements CardService {

    private final GlobalServiceHelper globalServiceHelper;

    @Autowired
    public CardServiceImpl(GlobalServiceHelper globalServiceHelper) {
        this.globalServiceHelper = globalServiceHelper;
    }

    @Override
    public CardResponse addNewCard(CardRequest cardRequest) {
        CardResponse cardResponse = new CardResponse();
        try {
            cardResponse = globalServiceHelper.addNewCardForExistingCustomer(cardRequest);

        } catch (Exception e) {

        }
        return cardResponse;
    }
}
