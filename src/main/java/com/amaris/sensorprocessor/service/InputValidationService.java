package com.amaris.sensorprocessor.service;

import com.amaris.sensorprocessor.entity.Gateway;
import com.amaris.sensorprocessor.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InputValidationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void isValidInputId(String id) {
        if (id == null || !id.matches("^[a-zA-Z0-9_-]+$")) {
            logger.error("ID invalide : format non autorisé.");
            System.out.println("\u001B[31m" + "ID invalide : format non autorisé." +
                    "\u001B[0m");
            throw new CustomException("problem with the ID field");
        }
    }

    public void isValidIp(String ip) {
        if (ip == null || !ip.matches("^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.|$)){4}$")) {
            logger.error("IP invalide : format non autorisé.");
            System.out.println("\u001B[31m" + "IP invalide : format non autorisé." +
                    "\u001B[0m");
            throw new CustomException("problem with the IP field");
        }
    }

    public void isValidDate(String date) {
        if (date == null || !date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            logger.error("Date invalide : format non autorisé.");
            System.out.println("\u001B[31m" + "Date invalide : format non autorisé." +
                    "\u001B[0m");
            throw new CustomException("problem with the Date field");
        }
    }

    public void isValidTextInput(String input) {
        if (input == null || !input.matches("^[\\p{L}0-9 ,:\\-_'\"]+$")) {
            logger.error("Text invalide : format non autorisé.");
            System.out.println("\u001B[31m" + "Text invalide : format non autorisé." +
                    "\u001B[0m");
            throw new CustomException("problem with the text field");
        }
    }

    public void isDigitsOnly(Integer input) {
        if (input == null || !String.valueOf(input).matches("^\\d+$")) {
            logger.error("Nombre invalide : format non autorisé.");
            System.out.println("\u001B[31m" + "Nombre invalide : format non autorisé." +
                    "\u001B[0m");
            throw new CustomException("problem with the number field");
        }
    }

    public void validateGateway(Gateway gateway) {
        if (gateway == null) {
            logger.error("La gateway est inexistante.");
            System.out.println("\u001B[31m" + "La gateway est inexistante." +
                    "\u001B[0m");
            throw new CustomException("Invalid gateway for input tests.");
        }
        isValidInputId(gateway.getIdGateway());
        isValidIp(gateway.getIpAddress());
        isValidDate(gateway.getCommissioningDate());
        isValidTextInput(gateway.getBuildingName());
        isDigitsOnly(gateway.getFloor());
        if (gateway.getLocation() != null) {
            isValidTextInput(gateway.getLocation());
        }
    }

}
