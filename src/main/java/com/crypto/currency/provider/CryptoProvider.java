package com.crypto.currency.provider;

import java.util.List;
import java.util.Map;

// Strategy Interface
public interface CryptoProvider {
    /**
     * Returns a map of (quoteSymbol -> exchangeRate) for the given baseSymbol.
     */
    Map<String, Double> getRatesForCurrency(String baseSymbol, List<String> rates);

}

