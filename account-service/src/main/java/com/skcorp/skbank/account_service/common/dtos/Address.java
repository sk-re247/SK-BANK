package com.skcorp.skbank.account_service.common.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Address {

    @JsonProperty("doorNo")
    private String doorNo;

    @JsonProperty("addressLineOne")
    private String addressLineOne;

    @JsonProperty("addressLineTwo")
    private String addressLineTwo;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("pinCode")
    private Integer pinCode;

    @JsonProperty("usage")
    private String usage;
}
