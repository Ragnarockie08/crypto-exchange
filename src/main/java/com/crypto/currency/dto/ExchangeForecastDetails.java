package com.crypto.currency.dto;

import lombok.*;

import java.math.BigDecimal;

@Value
public class ExchangeForecastDetails {
    BigDecimal rate;
    BigDecimal amount;
    BigDecimal result;
    BigDecimal fee;
}

