package com.crypto.currency.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

import java.util.List;

@Value
public class ExchangeRequest {

    @NotBlank(message = "Source currency (from) must not be blank")
    String from;
    @NotEmpty(message = "Target currencies (to) must not be empty")
    List<String> to;
    @Min(value = 0, message = "Amount must be greater than or equal to 0")
    Double amount;
}

