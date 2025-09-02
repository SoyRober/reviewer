package com.reviewer.exception.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionUtils {

    private ExceptionUtils() {
    }

    public static String getStackTrace(Exception e) {

        StringBuilder error = new StringBuilder();

        try {
            error.append(e.getMessage()).append("\n");
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                error.append(stackTraceElement).append("\n");
                if (stackTraceElement.toString().toLowerCase().contains("holdhub")) return error.toString();
            }

        } catch (Exception ex) {
            log.error("getStackTrace || ERROR: {}", ex.getMessage());
        }
        return error.toString();
    }

}