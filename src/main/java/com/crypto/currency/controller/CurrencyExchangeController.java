package com.crypto.currency.controller;

import com.crypto.currency.dto.CurrencyRatesResponse;
import com.crypto.currency.dto.ExchangeRequest;
import com.crypto.currency.dto.ExchangeResponse;
import com.crypto.currency.service.CurrencyRateService;
import com.crypto.currency.service.ExchangeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currencies")
public class CurrencyExchangeController {

    private final CurrencyRateService currencyRateService;
    private final ExchangeService exchangeService;

    public CurrencyExchangeController(CurrencyRateService currencyRateService,
                                      ExchangeService exchangeService) {
        this.currencyRateService = currencyRateService;
        this.exchangeService = exchangeService;
    }

    /**
     * GET /currencies/{currency}?filter[]=X&filter[]=Y
     * Example: /currencies/BTC?filter[]=USDT&filter[]=ETH
     */
    @GetMapping("/{currency}")
    public CurrencyRatesResponse getCurrencyRates(
            @PathVariable("currency") String currency,
            @RequestParam(value = "filter[]", required = false) List<String> filters
    ) {
        return currencyRateService.getRates(currency, filters);
    }

    /**
     * POST /currencies/exchange
     * Body example: {"from":"currencyA","to":["currencyB","currencyC"],"amount":121}
     */
    @PostMapping("/exchange")
    public ExchangeResponse exchange(@RequestBody ExchangeRequest request) {
        return exchangeService.calculateExchange(request);
    }
}
