package com.skcorp.skbank.card_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = { "com.skcorp.skbank.card_service", "com.skcorp.skbank.skb_common"})
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = {"com.skcorp.skbank.skb_common.repositories.cards"})
@EntityScan(basePackages = {"com.skcorp.skbank.skb_common.entities.cards"})
public class CardServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardServiceApplication.class, args);
	}

}
