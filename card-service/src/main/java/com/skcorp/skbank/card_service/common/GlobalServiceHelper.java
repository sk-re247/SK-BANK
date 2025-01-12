package com.skcorp.skbank.card_service.common;

import com.skcorp.skbank.card_service.client.models.CardRequest;
import com.skcorp.skbank.card_service.client.models.CardResponse;
import com.skcorp.skbank.card_service.client.models.CardTypeEnum;
import com.skcorp.skbank.card_service.service.client.AccountFeignClient;
import com.skcorp.skbank.skb_common.entities.cards.Card;
import com.skcorp.skbank.skb_common.entities.cards.CardAccXref;
import com.skcorp.skbank.skb_common.repositories.cards.CardAccXrefRepository;
import com.skcorp.skbank.skb_common.repositories.cards.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

@Service
public class GlobalServiceHelper {

    private final AccountFeignClient accountFeignClient;

    private static final Random random = new Random();

    private final CardRepository cardRepository;

    private final CardAccXrefRepository cardAccXrefRepository;

    @Autowired
    public GlobalServiceHelper(
            AccountFeignClient accountFeignClient,
            CardRepository cardRepository,
            CardAccXrefRepository cardAccXrefRepository) {
        this.accountFeignClient = accountFeignClient;
        this.cardAccXrefRepository = cardAccXrefRepository;
        this.cardRepository = cardRepository;
    }

    public CardResponse addNewCardForExistingCustomer(CardRequest cardRequest) throws Exception {
        CardResponse cardResponse = new CardResponse();
        String cardHolderName = null;
        try {
            ResponseEntity<String> response = accountFeignClient.fetchAccountHolderName(cardRequest.getAccountNumber(), cardRequest.getMobileumber());
            cardHolderName = response.getBody();

            Card card = new Card();
            card.setType(cardRequest.getCardType().toString());
            card.setCvv(generateCVV());
            card.setExpMonth(generateExpirationMonth());
            card.setExpYear(generateExpirationYear());
            card.setIsActive(true);
            card.setNum(generateCardNumber());
            card.setHolder(cardHolderName);
            card.setScheme(cardRequest.getScheme().toString());

            Card createdCard = cardRepository.save(card);

            CardAccXref accXref = new CardAccXref();
            accXref.setCard(createdCard);
            accXref.setAccountNumber(cardRequest.getAccountNumber());

            cardAccXrefRepository.save(accXref);

            cardResponse.setCardNumber(createdCard.getNum());
            cardResponse.setCardHolderName(cardHolderName);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return cardResponse;
    }

    private String generateCardNumber() {

        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            cardNumber.append(random.nextInt(10));
            // Add a space after every 4 digits for the format "xxxx xxxx xxxx xxxx"
            if ((i + 1) % 4 == 0 && i != 14) {
                cardNumber.append(" ");
            }
        }

        return cardNumber.toString();
    }

    // Generate random expiration month (1-12)
    public int generateExpirationMonth() {
        return random.nextInt(12) + 1;
    }

    public Long generateExpirationYear() {
        int currentYear = LocalDate.now().getYear();
        return (long) (random.nextInt(5) + currentYear);
    }

    public int generateCVV() {
        return random.nextInt(900) + 100;
    }
}
