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

    public static void logDatabaseError(Exception e) {
        String message = "Database problem : " + e.getMessage();
        logger.error("Database problem : ", e);
        System.out.println("\u001B[31m" + message + "\u001B[0m");
    }

//    public static void logErrorWithMessage(String context, String message) {
//        String fullMessage = context + " => " + message;
//        logger.error(fullMessage);
//        System.out.println("\u001B[31m" + fullMessage + "\u001B[0m");
//    }
//
//    public static void logErrorWithException(String context, Exception e) {
//        String fullMessage = context + " => " + e.getMessage();
//        logger.error(fullMessage, e);
//        System.out.println("\u001B[31m" + fullMessage + "\u001B[0m");
//    }

}
