package com.amaris.sensorprocessor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;

public final class LoggerUtil {

    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);

    public static void logWithBindingObjectError(BindingResult bindingResult, Exception e, String errorMessage, String gatewayValue, String nameBindingValue) {
        String valueToLog = (gatewayValue != null && !gatewayValue.isEmpty()) ? gatewayValue : "[no value]";
        logger.error("{} : {}", errorMessage, valueToLog, e);
        System.out.println("\u001B[31m" + errorMessage + " : " + valueToLog + " => " + e.getMessage() + "\u001B[0m");
        bindingResult.rejectValue(nameBindingValue, "Invalid." + nameBindingValue, errorMessage);
    }

    public static void logWithBindingError(BindingResult bindingResult, Exception e, String errorMessage, String gatewayValue, String nameBindingValue) {
        String valueToLog = (gatewayValue != null && !gatewayValue.isEmpty()) ? gatewayValue : "[no value]";
        logger.error("{} : {}", errorMessage, valueToLog, e);
        System.out.println("\u001B[31m" + errorMessage + " : " + valueToLog + " => " + e.getMessage() + "\u001B[0m");
        bindingResult.reject(nameBindingValue, errorMessage);
    }

    public static void logWithBindingObject(BindingResult bindingResult, String errorMessage, String gatewayValue, String nameBindingValue) {
        String valueToLog = (gatewayValue != null && !gatewayValue.isEmpty()) ? gatewayValue : "[no value]";
        logger.error("{} : {}", errorMessage, valueToLog);
        System.out.println("\u001B[31m" + errorMessage + " : " + valueToLog + "\u001B[0m");
        bindingResult.rejectValue(nameBindingValue, "Invalid." + nameBindingValue, errorMessage);
    }

    public static void logError(Exception e, String gatewayValue) {
        String valueToLog = (gatewayValue != null && !gatewayValue.isEmpty()) ? gatewayValue : "[no value]";
        logger.error("{}", valueToLog, e);
        System.out.println("\u001B[31m" + valueToLog + " => " + e.getMessage() + "\u001B[0m");
    }

}
