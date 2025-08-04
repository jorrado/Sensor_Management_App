package com.amaris.sensorprocessor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {

    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);

    public static void logInvalidateFormatError(String message) {
        String fullMessage = "Unauthorized format : " + message;
        logger.error(fullMessage);
        System.out.println("\u001B[31m" + fullMessage + "\u001B[0m");
    }

}
