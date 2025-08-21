package com.amaris.sensorprocessor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;

public final class LoggerUtil {

    private LoggerUtil() {}

    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);

    private static final String NO_VALUE = "[no value]";
    public static final String RED = "\u001B[31m";
    public static final String RESET = "\u001B[0m";

    public static void logWithBindingObjectError(BindingResult bindingResult, Exception e, String errorMessage, String gatewayValue, String nameBindingValue) {
        String valueToLog = (gatewayValue != null && !gatewayValue.isEmpty()) ? gatewayValue : NO_VALUE;
        logger.error("{} : {}", errorMessage, valueToLog, e);
        System.out.println(RED + errorMessage + " : " + valueToLog + " => " + e.getMessage() + RESET);
        bindingResult.rejectValue(nameBindingValue, "Invalid." + nameBindingValue, errorMessage);
    }

    public static void logWithBindingError(BindingResult bindingResult, Exception e, String errorMessage, String gatewayValue, String nameBindingValue) {
        String valueToLog = (gatewayValue != null && !gatewayValue.isEmpty()) ? gatewayValue : NO_VALUE;
        logger.error("{} : {}", errorMessage, valueToLog, e);
        System.out.println(RED + errorMessage + " : " + valueToLog + " => " + e.getMessage() + RESET);
        bindingResult.reject(nameBindingValue, errorMessage);
    }

    public static void logWithBindingObject(BindingResult bindingResult, String errorMessage, String gatewayValue, String nameBindingValue) {
        String valueToLog = (gatewayValue != null && !gatewayValue.isEmpty()) ? gatewayValue : NO_VALUE;
        logger.error("{} : {}", errorMessage, valueToLog);
        System.out.println(RED + errorMessage + " : " + valueToLog + RESET);
        bindingResult.rejectValue(nameBindingValue, "Invalid." + nameBindingValue, errorMessage);
    }

    public static void logError(Exception e, String gatewayValue) {
        String valueToLog = (gatewayValue != null && !gatewayValue.isEmpty()) ? gatewayValue : NO_VALUE;
        logger.error("{}", valueToLog, e);
        System.out.println(RED + valueToLog + " => " + e.getMessage() + RESET);
    }

}
