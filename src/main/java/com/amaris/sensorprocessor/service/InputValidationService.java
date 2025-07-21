package com.amaris.sensorprocessor.service;

import com.amaris.sensorprocessor.entity.Gateway;
import com.amaris.sensorprocessor.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InputValidationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Valide le champ 'gatewayId' selon les règles de format LoRaWAN.
     * Le format autorisé est : 3 à 36 caractères minuscules, chiffres ou tiret du 6.
     * Ne doit pas commencer ou finir par un tiret.
     * Pas de doubles tirets consécutifs.
     *
     * @param gatewayId l'identifiant à valider
     */
    public void isValidInputGatewayId(String gatewayId) {
        if (gatewayId == null || !gatewayId.matches("^(?!-)(?!.*--)[a-z0-9-]{3,36}(?<!-)$")) {
            logger.error("Unauthorized format : gatewayId invalid");
            System.out.println("\u001B[31m" + "Unauthorized format : gatewayId invalid" +
                    "\u001B[0m");
//            throw new CustomException("Unauthorized format : Gateway ID field");
        }
    }

    /**
     * Valide le format du 'gatewayEui' selon les règles de format LoRaWAN.
     * Le gatewayEui doit contenir exactement 16 caractères,
     * uniquement des chiffres (0-9) et des lettres majuscules (A-F).
     * Aucun espace, tiret ou autre séparateur n'est autorisé.
     *
     * @param gatewayEui la chaîne à valider
     */
    public void isValidInputGatewayEui(String gatewayEui) {
        if (gatewayEui == null || !gatewayEui.matches("^[0-9A-F]{16}$")) {
            logger.error("Unauthorized format : gatewayEui invalid");
            System.out.println("\u001B[31m" + "Unauthorized format : gatewayEui invalid" +
                    "\u001B[0m");
//            throw new CustomException("Unauthorized format : Gateway EUI field");
        }
    }

    /**
     * Vérifie que l'adresse IP est au format IPv4 valide.
     *
     * @param ipAddress l'adresse IP à valider
     * @throws CustomException si l'adresse IP est invalide ou nulle
     */
    public void isValidInputIpAddress(String ipAddress) {
        if (ipAddress == null || !ipAddress.matches("^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$")) {
            logger.error("Unauthorized format : ipAddress invalid");
            System.out.println("\u001B[31m" + "Unauthorized format : ipAddress invalid" +
                    "\u001B[0m");
//            throw new CustomException("Unauthorized format : IP Address field");
        }
    }

    /**
     * Valide un textes autorisant lettres (majuscules, minuscules, accents), chiffres,
     * espaces, et caractères spéciaux (-,'".), avec une longueur maximale de 50 caractères.
     *
     * @param str le nom à valider
     * @throws IllegalArgumentException si le format est invalide
     */
    public void isValidInputText(String str) {
        if (str == null || !str.matches("^[a-zA-ZÀ-ÖØ-öø-ÿ0-9\\s\\-,'\".]{1,50}$")) {
            logger.error("Unauthorized format : invalid text");
            System.out.println("\u001B[31m" + "Unauthorized format : invalid text" +
                    "\u001B[0m");
//            throw new CustomException("Unauthorized format : text field");
        }
    }

    public void isValidInputFloorNumber(Integer floorNumber) {
        if (floorNumber == null || floorNumber < 0 || floorNumber > 99) {
            logger.error("Unauthorized format : floorNumber invalid");
            System.out.println("\u001B[31m" + "Unauthorized format : floorNumber invalid" + "\u001B[0m");
//        throw new CustomException("Unauthorized format : Floor Number field");
        }
    }

    /**
     * Valide une coordonnée géographique au format décimal.
     * Accepte un entier de 1 à 3 chiffres, avec optionnellement un point
     * suivi de 1 à 10 chiffres décimaux.
     *
     * @param coordinate La valeur à valider sous forme de chaîne.
     * @throws CustomException si le format est invalide.
     */
    public void isValidInputAntenna(Double coordinate) {
        if (coordinate == null || !(String.valueOf(coordinate).matches("^\\d{1,3}(\\.\\d{1,10})?$"))) {
            logger.error("Unauthorized format : coordinate invalid");
            System.out.println("\u001B[31m" + "Unauthorized format : coordinate invalid" +
                    "\u001B[0m");
//            throw new CustomException("Unauthorized format : coordinate field");
        }
    }

    public void validateGateway(Gateway gateway) {
        if (gateway == null) {
            logger.error("The gateway does not exist");
            System.out.println("\u001B[31m" + "The gateway does not exist" +
                    "\u001B[0m");
            throw new CustomException("The gateway does not exist");
        }
        isValidInputGatewayId(gateway.getGatewayId());
        isValidInputGatewayEui(gateway.getGatewayEui());
        isValidInputIpAddress(gateway.getIpAddress());
        isValidInputText(gateway.getBuildingName());
        isValidInputFloorNumber(gateway.getFloorNumber());
        isValidInputText(gateway.getLocationDescription());
        if (gateway.getAntennaLatitude() != null) {
            isValidInputAntenna(gateway.getAntennaLatitude());
        }
        if (gateway.getAntennaLongitude() != null) {
            isValidInputAntenna(gateway.getAntennaLongitude());
        }
        if (gateway.getAntennaAltitude() != null) {
            isValidInputAntenna(gateway.getAntennaAltitude());
        }
    }

}
