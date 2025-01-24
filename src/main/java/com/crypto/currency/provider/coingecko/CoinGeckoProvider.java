package com.crypto.currency.provider.coingecko;


import com.crypto.currency.provider.CryptoProvider;
import com.crypto.currency.provider.coingecko.client.CoinGeckoApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CoinGeckoProvider implements CryptoProvider {

    private final CoinGeckoApiClient apiClient;
    private static final String DEFAULT_RATE = "usd";

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

    //TODO: create coinId mapping service/enum for wide param acceptance criteria
    private String mapToCoinGeckoId(String symbol) {
        return switch (symbol.toUpperCase()) {
            case "BTC" -> "bitcoin";
            case "ETH" -> "ethereum";
            case "USDT" -> "tether";
            case "ADA" -> "cardano";
            default -> symbol.toLowerCase();
        };
    }
}

