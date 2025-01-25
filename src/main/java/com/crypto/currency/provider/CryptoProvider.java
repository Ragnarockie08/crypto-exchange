package com.crypto.currency.provider;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

// Strategy Interface
public interface CryptoProvider {
    /**
     * Returns a map of (quoteSymbol -> exchangeRate) for the given baseSymbol.
     */
    Map<String, BigDecimal> getRatesForCurrency(String baseSymbol, List<String> rates);

}

