package com.amaris.sensorprocessor.service;

import com.amaris.sensorprocessor.constant.FrequencyPlan;
import com.amaris.sensorprocessor.entity.Gateway;
import com.amaris.sensorprocessor.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Arrays;

import static com.amaris.sensorprocessor.util.LoggerUtil.logInvalidateFormatError;

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
    public void isValidInputGatewayId(String gatewayId, BindingResult bindingResult) {
        if (gatewayId == null || !gatewayId.matches("^(?!-)(?!.*--)[a-z0-9-]{3,36}(?<!-)$")) {
            logInvalidateFormatError("gatewayId invalid");
            bindingResult.rejectValue("gatewayId", "Invalid.gatewayId", "Gateway ID format is invalid");
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
    public void isValidInputGatewayEui(String gatewayEui, BindingResult bindingResult) {
        if (gatewayEui == null || !gatewayEui.matches("^[0-9A-F]{16}$")) {
            logInvalidateFormatError("gatewayEui invalid");
            bindingResult.rejectValue("gatewayEui", "Invalid.gatewayEui", "Gateway EUI format is invalid");
        }
    }

    /**
     * Vérifie que l'adresse IP est au format IPv4 valide.
     *
     * @param ipAddress l'adresse IP à valider
     * @throws CustomException si l'adresse IP est invalide ou nulle
     */
    public void isValidInputIpAddress(String ipAddress, BindingResult bindingResult) {
        if (ipAddress == null || !ipAddress.matches("^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$")) {
            logInvalidateFormatError("ipAddress invalid");
            bindingResult.rejectValue("ipAddress", "Invalid.ipAddress", "IP Address format is invalid");
        }
    }

    /**
     * Valide si frequencyPlan est non nul et présent dans l'enum FrequencyPlan.
     * Sinon, le réinitialise et ajoute une erreur de validation.
     *
     * @param gateway l'objet Gateway à valider
     * @param bindingResult l'objet pour enregistrer les erreurs de validation
     */
    public void isValidDropDownMenuFrequencyPlan(Gateway gateway, BindingResult bindingResult) {
        if (gateway.getFrequencyPlan() != null) {
            boolean exists = Arrays.stream(FrequencyPlan.values())
                    .anyMatch(value -> value.getDescription()
                            .equals(gateway.getFrequencyPlan()));
            if (!exists) {
                gateway.setFrequencyPlan(null);
                logInvalidateFormatError("frequencyPlan invalid");
                bindingResult.rejectValue("frequencyPlan", "Invalid.frequencyPlan", "Frequency Plan format is invalid");
            }
        }
    }

    /**
     * Valide un textes autorisant lettres (majuscules, minuscules, accents), chiffres,
     * espaces, et caractères spéciaux (-,'".), avec une longueur maximale de 50 caractères.
     *
     * @param buildingName le nom à valider
     * @throws IllegalArgumentException si le format est invalide
     */
    public void isValidInputBuildingName(String buildingName, BindingResult bindingResult) {
        if (buildingName == null || !buildingName.matches("^[a-zA-ZÀ-ÖØ-öø-ÿ0-9\\s\\-,'\".]{1,50}$")) {
            logInvalidateFormatError("invalid text of Building Name");
            bindingResult.rejectValue("buildingName", "Invalid.buildingName", "Text format of Building Name is invalid");
        }
    }

    /**
     * Valide que floorNumber est entre 0 et 99 inclus.
     * Sinon, log une erreur et ajoute une erreur de validation.
     *
     * @param floorNumber le numéro d'étage à valider
     * @param bindingResult l'objet pour enregistrer les erreurs de validation
     */
    public void isValidInputFloorNumber(Integer floorNumber, BindingResult bindingResult) {
        if (floorNumber == null || floorNumber < -10 || floorNumber > 99) {
            logInvalidateFormatError("floorNumber invalid");
            bindingResult.rejectValue("floorNumber", "Invalid.floorNumber", "Text format of Floor Number is invalid");
        }
    }

    /**
     * Valide un textes autorisant lettres (majuscules, minuscules, accents), chiffres,
     * espaces, et caractères spéciaux (-,'".), avec une longueur maximale de 50 caractères.
     *
     * @param locationDescription le nom à valider
     * @throws IllegalArgumentException si le format est invalide
     */
    public void isValidInputLocationDescription(String locationDescription, BindingResult bindingResult) {
        if (locationDescription == null || !locationDescription.matches("^[a-zA-ZÀ-ÖØ-öø-ÿ0-9\\s\\-,'\".]{1,50}$")) {
            logInvalidateFormatError("invalid text of Location Description");
            bindingResult.rejectValue("locationDescription", "Invalid.locationDescription", "Text format of Location Description is invalid");
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
            throw new CustomException("Unauthorized format : coordinate field");
        }
    }

    public void validateGatewayForCreateForm(Gateway gateway, BindingResult bindingResult) {
        isValidInputGatewayId(gateway.getGatewayId(), bindingResult);
        isValidInputGatewayEui(gateway.getGatewayEui(), bindingResult);
        isValidInputIpAddress(gateway.getIpAddress(), bindingResult);
        isValidDropDownMenuFrequencyPlan(gateway, bindingResult);
        isValidInputBuildingName(gateway.getBuildingName(), bindingResult);
        isValidInputFloorNumber(gateway.getFloorNumber(), bindingResult);
        isValidInputLocationDescription(gateway.getLocationDescription(), bindingResult);
//        if (gateway.getAntennaLatitude() != null) {
//            isValidInputAntenna(gateway.getAntennaLatitude());
//        }
//        if (gateway.getAntennaLongitude() != null) {
//            isValidInputAntenna(gateway.getAntennaLongitude());
//        }
//        if (gateway.getAntennaAltitude() != null) {
//            isValidInputAntenna(gateway.getAntennaAltitude());
//        }
    }

    public void validateGatewayForUpdateForm(Gateway gateway, BindingResult bindingResult) {
        isValidInputGatewayId(gateway.getGatewayId(), bindingResult);
        isValidInputIpAddress(gateway.getIpAddress(), bindingResult);
        isValidDropDownMenuFrequencyPlan(gateway, bindingResult);
        isValidInputBuildingName(gateway.getBuildingName(), bindingResult);
        isValidInputFloorNumber(gateway.getFloorNumber(), bindingResult);
        isValidInputLocationDescription(gateway.getLocationDescription(), bindingResult);
    }

}
