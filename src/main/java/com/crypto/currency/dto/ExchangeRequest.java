package com.crypto.currency.dto;

import lombok.Value;

import java.util.List;

@Value
public class ExchangeRequest {

    String from;
    List<String> to;
    Double amount;
}

