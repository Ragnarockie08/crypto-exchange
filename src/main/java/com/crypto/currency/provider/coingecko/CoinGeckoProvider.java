package com.crypto.currency.provider.coingecko;


import com.crypto.currency.provider.CryptoRateProvider;
import com.crypto.currency.provider.coingecko.client.CoinGeckoApiClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class CoinGeckoProvider implements CryptoRateProvider {

    private final CoinGeckoApiClient apiClient;
    private static final String DEFAULT_RATE = "usdt";
    public CoinGeckoProvider(CoinGeckoApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Returns a map of (quote -> rate) for the given base crypto symbol.
     * Example: baseSymbol = "BTC" => call coinGeckoId = "bitcoin", vs_currencies = "usd,eth,usdt,eur"
     */
    @Override
    public Map<String, Double> getRatesForCurrency(String baseSymbol, List<String> rates) {
        String coinGeckoId = mapToCoinGeckoId(baseSymbol);

        if (rates == null || rates.isEmpty()) {
            rates = List.of(DEFAULT_RATE);
        }
        Map<String, Map<String, Double>> response = apiClient.getSimplePrice(coinGeckoId, rates);

        if (response == null || !response.containsKey(coinGeckoId)) {
            return Collections.emptyMap();
        }

        return response.get(coinGeckoId);
    }

    /**
     * Maps internal "BTC", "ETH", etc. to CoinGecko's "bitcoin", "ethereum", "tether", etc.
     */
    private String mapToCoinGeckoId(String symbol) {
        switch (symbol.toUpperCase()) {
            case "BTC":
                return "bitcoin";
            case "ETH":
                return "ethereum";
            case "USDT":
                return "tether";
            default:
                return symbol.toLowerCase();
        }
    }
}

