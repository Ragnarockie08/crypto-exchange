package com.crypto.currency.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ExchangeForecastDetails {
    private final Double rate;
    private final Double amount;
    private final Double result;
    private final Double fee;
}

