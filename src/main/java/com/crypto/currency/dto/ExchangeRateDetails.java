package com.crypto.currency.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ExchangeRateDetails {
    private Double rate;
    private Double amount;
    private Double result;
    private Double fee;
}

