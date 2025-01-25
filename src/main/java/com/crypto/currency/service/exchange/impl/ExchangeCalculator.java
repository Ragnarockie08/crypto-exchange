package com.crypto.currency.service.exchange.impl;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ExchangeCalculator {

    private static final BigDecimal FEE_PERCENTAGE = BigDecimal.valueOf(0.01);

    public BigDecimal calculateFee(BigDecimal amount) {
        return amount.multiply(FEE_PERCENTAGE).stripTrailingZeros();
    }

    public BigDecimal calculateNetAmount(BigDecimal amount, BigDecimal fee) {
        return amount.subtract(fee).stripTrailingZeros();
    }

    public BigDecimal calculateResult(BigDecimal netAmount, BigDecimal rate) {
        return netAmount.multiply(rate);
    }
}
