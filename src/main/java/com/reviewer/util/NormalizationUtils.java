package com.reviewer.util;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@NoArgsConstructor
public class NormalizationUtils {
    public static String normalizeAddress(String address) {
        return address == null ? null : address.toLowerCase(Locale.ROOT).trim();
    }
}
