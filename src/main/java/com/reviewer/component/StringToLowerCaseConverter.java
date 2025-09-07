package com.reviewer.component;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class StringToLowerCaseConverter implements Converter<String, String> {
    @Override
    public String convert(String source) {
        return source.toLowerCase(Locale.ROOT);
    }
}
