package com.crypto.currency.utils;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class StringUtils {


    public static String toCommaSeparated(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        return items.stream()
                .map(String::trim)
                .collect(Collectors.joining(","));
    }
}
