package com.skcorp.skbank.card_service.service.client;

import com.skcorp.skbank.card_service.common.configs.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "card-service", url = "http://localhost:8082/AccountService", configuration = FeignConfig.class)
public interface AccountFeignClient {

    @GetMapping(path = "/account/holder-name", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> fetchAccountHolderName(@RequestParam String accountNumber, @RequestParam String mobileNumber);
}
