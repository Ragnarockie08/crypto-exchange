package com.crypto.currency.controller;


import com.crypto.currency.config.SecurityConfig;
import com.crypto.currency.dto.ExchangeForecastDetails;
import com.crypto.currency.dto.ExchangeRequest;
import com.crypto.currency.dto.ExchangeResponse;
import com.crypto.currency.service.rates.CurrencyRateService;
import com.crypto.currency.service.exchange.ExchangeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//TODO: setup BaseWebMvc controller for all future controllers
@Import(SecurityConfig.class)
@WebMvcTest(CurrencyExchangeController.class)
class CurrencyExchangeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CurrencyRateService currencyRateService;

    @MockitoBean
    private ExchangeService exchangeService;

    @Test
    void shouldReturnExchangeResponse_WhenValidRequest() throws Exception {
        //given
        String requestJson = """
                        {
                            "from": "btc",
                            "to": ["usd", "eth"],
                            "amount": 1.0
                        }
                    """;

        ExchangeResponse mockResponse = new ExchangeResponse();
        mockResponse.setFrom("btc");
        mockResponse.setConversions(Map.of(
                "usd", new ExchangeForecastDetails(105300.0, 1.0, 104300.0, 0.01),
                "eth", new ExchangeForecastDetails(31.007, 1.0, 31.005, 0.01)
        ));
        //when
        when(exchangeService.calculateExchange(Mockito.any(ExchangeRequest.class))).thenReturn(mockResponse);

        //then
        mockMvc.perform(post("/currencies/exchange")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("btc"))
                .andExpect(jsonPath("$.conversions.usd.rate").value(105300.0))
                .andExpect(jsonPath("$.conversions.usd.result").value(104300.0))
                .andExpect(jsonPath("$.conversions.eth.rate").value(31.007))
                .andExpect(jsonPath("$.conversions.eth.result").value(31.005));
    }

    @Test
    void whenSourceCurrencyIsBlank_thenReturnsBadRequest() throws Exception {
        String invalidRequest = """
                            {
                                "from": "",
                                "to": ["usd", "eth"],
                                "amount": 100.0
                            }
                        """;

        mockMvc.perform(post("/currencies/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("from"))
                .andExpect(jsonPath("$.message").value("Source currency (from) must not be blank"));
    }

    @Test
    void whenTargetCurrenciesAreEmpty_thenReturnsBadRequest() throws Exception {
        String invalidRequest = """
                            {
                                "from": "btc",
                                "to": [],
                                "amount": 100.0
                            }
                        """;

        mockMvc.perform(post("/currencies/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("to"))
                .andExpect(jsonPath("$.message").value("Target currencies (to) must not be empty"));
    }

    @Test
    void whenAmountIsNegative_thenReturnsBadRequest() throws Exception {
        String invalidRequest = """
                            {
                                "from": "btc",
                                "to": ["usd", "eth"],
                                "amount": -10.0
                            }
                        """;

        mockMvc.perform(post("/currencies/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("amount"))
                .andExpect(jsonPath("$.message").value("Amount must be greater than or equal to 0"));
    }
}
