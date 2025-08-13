package com.amaris.sensorprocessor.util;

import com.amaris.sensorprocessor.entity.Gateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public final class LoggerUtil {

    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);

    public static void logWithBindingObjectError(Gateway gateway, BindingResult bindingResult,
                WebClientResponseException e, String errorMessage, String gatewayValue, String nameBindingValue) {
        logger.error("{} : {}", errorMessage, gatewayValue, e);
        System.out.println("\u001B[31m" + errorMessage + " : " + gatewayValue + " => " + e.getMessage() + "\u001B[0m");
        bindingResult.rejectValue(nameBindingValue, "Invalid." + nameBindingValue, errorMessage);
    }

    public static void logWithBindingError() {

    }

    public static void logInvalidateFormatError(String message) {
        String fullMessage = "Unauthorized format : " + message;
        logger.error(fullMessage);
        System.out.println("\u001B[31m" + fullMessage + "\u001B[0m");
    }

    public static void logDatabaseError(Exception e) {
        String message = "Database problem : " + e.getMessage();
        logger.error("Database problem : ", e);
        System.out.println("\u001B[31m" + message + "\u001B[0m");
    }

}
