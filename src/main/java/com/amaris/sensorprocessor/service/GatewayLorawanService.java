package com.amaris.sensorprocessor.service;

import com.amaris.sensorprocessor.constant.Constants;
import com.amaris.sensorprocessor.entity.Gateway;
import com.amaris.sensorprocessor.entity.LorawanGatewayData;
import com.amaris.sensorprocessor.entity.LorawanGatewayUpdateData;
import com.amaris.sensorprocessor.repository.GatewayLorawanDao;
import com.amaris.sensorprocessor.util.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class GatewayLorawanService {

    private final GatewayLorawanDao gatewayLorawanDao;

    @Autowired
    public GatewayLorawanService(GatewayLorawanDao gatewayLorawanDao) {
        this.gatewayLorawanDao = gatewayLorawanDao;
    }

    public void saveGatewayInLorawan(Gateway gateway, BindingResult bindingResult) {
        try {
            LorawanGatewayData lorawanGatewayData = GatewayLorawanService.toLorawanGatewayData(gateway);
            String jsonResult = gatewayLorawanDao.insertGatewayInLorawan(lorawanGatewayData);
            gateway.setCreatedAt(extractAndFormatCreatedAt(jsonResult));
        } catch (WebClientResponseException e) {
            String errorBody = e.getResponseBodyAsString();
            if (errorBody.contains("\"code\":6") && errorBody.contains("\"name\":\"id_taken\"")) {
                LoggerUtil.logWithBindingObjectError(bindingResult, e, Constants.GATEWAY_ID_EXISTS, gateway.getGatewayId(), Constants.BINDING_GATEWAY_ID);
            } else if (errorBody.contains("\"code\":6") && errorBody.contains("\"name\":\"gateway_eui_taken\"")) {
                LoggerUtil.logWithBindingObjectError(bindingResult, e, Constants.GATEWAY_EUI_EXISTS, gateway.getGatewayEui(), Constants.BINDING_GATEWAY_EUI);
            }
        } catch (Exception e) {
            LoggerUtil.logWithBindingObjectError(bindingResult, e, Constants.LORAWAN_PROBLEM, null, Constants.BINDING_LORAWAN_PROBLEM);
        }
    }

    public void deleteGatewayInLorawan(String gatewayId, BindingResult bindingResult) {
        try {
            gatewayLorawanDao.deleteGatewayInLorawan(gatewayId);
        } catch (WebClientResponseException e) {
            int status = e.getStatusCode().value();
            String errorBody = e.getResponseBodyAsString();
            if (status == 403 && errorBody.contains("\"code\":7")) {
                if (errorBody.contains("\"name\":\"no_gateway_rights\"")) {
                    LoggerUtil.logWithBindingError(bindingResult, e, Constants.GATEWAY_NOT_FOUND, gatewayId, Constants.BINDING_GATEWAY_ID);
                } else if (errorBody.contains("\"name\":\"insufficient_gateway_rights\"")) {
                    LoggerUtil.logWithBindingError(bindingResult, e, Constants.PERMISSION_DENIED, gatewayId, Constants.BINDING_PERMISSION_DENIED);
                } else {
                    LoggerUtil.logWithBindingError(bindingResult, e, Constants.GATEWAY_PROBLEM, gatewayId, Constants.BINDING_GATEWAY_PROBLEM);
                }
            }
        } catch (Exception e) {
            LoggerUtil.logWithBindingError(bindingResult, e, Constants.LORAWAN_PROBLEM, null, Constants.BINDING_LORAWAN_PROBLEM);
        }
    }

    public void updateGatewayInLorawan(Gateway gateway, BindingResult bindingResult) {
        LorawanGatewayUpdateData updateData = toLorawanGatewayUpdateData(gateway);
        try {
            gatewayLorawanDao.updateGatewayInLorawan(updateData, gateway.getGatewayId());
        } catch (WebClientResponseException e) {
            int status = e.getStatusCode().value();
            String errorBody = e.getResponseBodyAsString();
            if (status == 403 && errorBody.contains("\"code\":7")) {
                if (errorBody.contains("\"name\":\"insufficient_gateway_rights\"")) {
                    LoggerUtil.logWithBindingError(bindingResult, e, Constants.PERMISSION_DENIED, gateway.getGatewayId(), Constants.BINDING_PERMISSION_DENIED);
                } else {
                    LoggerUtil.logWithBindingObjectError(bindingResult, e, Constants.GATEWAY_NOT_FOUND, gateway.getGatewayId(), Constants.BINDING_GATEWAY_ID);
                }
            }
        } catch (Exception e) {
            LoggerUtil.logWithBindingObjectError(bindingResult, e, Constants.LORAWAN_PROBLEM, null, Constants.BINDING_LORAWAN_PROBLEM);
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
     * Convertit un objet {@link Gateway} en {@link LorawanGatewayUpdateData}
     * pour mettre à jour uniquement le plan de fréquence sur le serveur LoRaWAN.
     *
     * @param gateway l’objet Gateway à convertir
     * @return l’objet LorawanGatewayUpdateData prêt pour la mise à jour
     */
    private static LorawanGatewayUpdateData toLorawanGatewayUpdateData(Gateway gateway) {
        LorawanGatewayUpdateData updateData = new LorawanGatewayUpdateData();
        LorawanGatewayUpdateData.GatewayPayload payload = new LorawanGatewayUpdateData.GatewayPayload();
        LorawanGatewayUpdateData.GatewayPayload.Ids ids = new LorawanGatewayUpdateData.GatewayPayload.Ids();

        ids.setGateway_id(gateway.getGatewayId());

        payload.setIds(ids);
        payload.setFrequency_plan_ids(new String[]{gateway.getFrequencyPlan()});

        updateData.setGateway(payload);
        updateData.setField_mask("frequency_plan_ids");

        return updateData;
    }

    /**
     * Extrait la valeur de "created_at" d’un JSON String et la formate au format "yyyy-MM-dd".
     * Si absente, utilise la date/heure UTC actuelle.
     *
     * @param json le JSON en tant que chaîne
     * @return la date formatée "yyyy-MM-dd"
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
        ZoneId systemZone = ZoneId.systemDefault();
        ZonedDateTime zdt = ZonedDateTime.parse(dateStr);
        ZonedDateTime localTime = zdt.withZoneSameInstant(systemZone);
        return localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

}
