package com.crypto.currency.dto;

import lombok.*;

@Value
public class ExchangeForecastDetails {
    Double rate;
    Double amount;
    Double result;
    Double fee;
}

