package com.skcorp.skbank.skb_common.repositories.cards;

import com.skcorp.skbank.skb_common.entities.cards.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

}
