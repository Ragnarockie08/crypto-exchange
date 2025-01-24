package com.crypto.currency.service.exchange.impl;

import org.springframework.stereotype.Component;

@Component
public class ExchangeRateCalculator {

    private static final double FEE_PERCENTAGE = 0.01;

    public double calculateFee(double amount) {
        return amount * FEE_PERCENTAGE;
    }

    public double calculateNetAmount(double amount, double fee) {
        return amount - fee;
    }

    public double calculateResult(double netAmount, double rate) {
        return netAmount * rate;
    }
}
