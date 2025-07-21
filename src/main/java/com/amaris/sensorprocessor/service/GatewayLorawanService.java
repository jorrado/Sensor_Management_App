package com.amaris.sensorprocessor.service;

import com.amaris.sensorprocessor.entity.Gateway;
import com.amaris.sensorprocessor.entity.LorawanGatewayData;
import com.amaris.sensorprocessor.repository.GatewayLorawanDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class GatewayLorawanService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GatewayLorawanDao gatewayLorawanDao;

    @Autowired
    public GatewayLorawanService(GatewayLorawanDao gatewayLorawanDao) {
        this.gatewayLorawanDao = gatewayLorawanDao;
    }

    public void saveGatewayInLorawan(Gateway gateway) {
        try {
            LorawanGatewayData lorawanGatewayData = GatewayLorawanService.toLorawanGatewayData(gateway);
            String jsonResult = gatewayLorawanDao.insertGatewayInLorawan(lorawanGatewayData);
            gateway.setCreatedAt(extractAndFormatCreatedAt(jsonResult));
        } catch (WebClientResponseException e) {
            String errorBody = e.getResponseBodyAsString();
            if (errorBody.contains("\"code\":6") && errorBody.contains("\"name\":\"id_taken\"")) {
                logger.error("Gateway ID already exists : {}", gateway.getGatewayId(), e);
                System.out.println("\u001B[31m" + "Gateway ID already exists : " + gateway.getGatewayId() + " " +
                        e.getMessage() + "\u001B[0m");
//            throw new CustomException("Gateway ID already exists");
            } else if (errorBody.contains("\"code\":6") && errorBody.contains("\"name\":\"gateway_eui_taken\"")) {
                logger.error("Gateway EUI already exists : {}", gateway.getGatewayEui(), e);
                System.out.println("\u001B[31m" + "Gateway EUI already exists : " + gateway.getGatewayEui() + " " +
                        e.getMessage() + "\u001B[0m");
//            throw new CustomException("Gateway EUI already exists");
            }
        } catch (Exception e) {
            logger.error("Lorawan problem", e);
            System.out.println("\u001B[31m" + "Lorawan problem : " +
                    e.getMessage() + "\u001B[0m");
//            throw new CustomException("Lorawan problem");
        }
    }

    public void deleteGatewayInLorawan(String gatewayId) {
        try {
            gatewayLorawanDao.deleteGatewayById(gatewayId);
        } catch (WebClientResponseException e) {
            int status = e.getStatusCode().value();
            if (status == 403 || status == 404) {
                logger.error("Deletion failed because Gateway ID not found : {}", gatewayId);
                System.out.println("\u001B[31m" + "Deletion failed because Gateway ID not found : " + gatewayId
                        + "\u001B[0m");
                // throw new CustomException("Deletion failed");
            }
        } catch (Exception e) {
            logger.error("Lorawan problem", e);
            System.out.println("\u001B[31m" + "Lorawan problem : " +
                    e.getMessage() + "\u001B[0m");
//            throw new CustomException("Lorawan problem");
        }
    }

    /**
     * Convertit un objet Gateway en LorawanGatewayData DTO adapté pour l’API LoRaWAN.
     *
     * @param gateway l’objet Gateway source
     * @return un objet LorawanGatewayData
     */
    private static LorawanGatewayData toLorawanGatewayData(Gateway gateway) {
        LorawanGatewayData data = new LorawanGatewayData();
        LorawanGatewayData.GatewayPayload payload = new LorawanGatewayData.GatewayPayload();
        LorawanGatewayData.GatewayPayload.Ids ids = new LorawanGatewayData.GatewayPayload.Ids();

        ids.setGateway_id(gateway.getGatewayId());
        ids.setEui(gateway.getGatewayEui());

        payload.setIds(ids);
        payload.setFrequency_plan_id(gateway.getFrequencyPlan());

        data.setGateway(payload);

        return data;
    }

    /**
     * Extrait la valeur de "created_at" d’un JSON String et la formate au format "yyyy-MM-dd HH:mm:ss".
     * Si absente, utilise la date/heure UTC actuelle.
     *
     * @param json le JSON en tant que chaîne
     * @return la date formatée "yyyy-MM-dd HH:mm:ss"
     */
    public String extractAndFormatCreatedAt(String json) {
        String key = "\"created_at\":\"";
        int start = json.indexOf(key);
        String dateStr;
        if (start == -1) {
            dateStr = OffsetDateTime.now().toString();
        } else {
            start += key.length();
            int end = json.indexOf("\"", start);
            dateStr = (end == -1) ? OffsetDateTime.now().toString() : json.substring(start, end);
        }
        OffsetDateTime odt = OffsetDateTime.parse(dateStr);
        return odt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}
