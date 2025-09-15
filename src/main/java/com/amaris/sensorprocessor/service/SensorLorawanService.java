package com.amaris.sensorprocessor.service;

import com.amaris.sensorprocessor.entity.LorawanSensorData;
import com.amaris.sensorprocessor.entity.LorawanSensorUpdateData;
import com.amaris.sensorprocessor.entity.Sensor;
import com.amaris.sensorprocessor.repository.SensorLorawanDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorLorawanService {

    private final SensorLorawanDao sensorLorawanDao;

    /* ===== CREATE ===== */
    public void createDevice(LorawanSensorData data) {
        String resp = sensorLorawanDao.insertSensorInLorawan(data);
        log.info("[LoRaWAN] created end-device: {}", resp);
    }

    /* ===== UPDATE (PUT /{sensorId}) ===== */
    public void updateDevice(String sensorId, LorawanSensorUpdateData data) {
        sensorLorawanDao.updateSensorInLorawan(data, sensorId);
        log.info("[LoRaWAN] updated end-device {}", sensorId);
    }

    /* ===== DELETE (DELETE /application/{applicationId}/devices/{sensorId}) ===== */
    public void deleteDevice(String idGateway, String sensorId) {
        String applicationId = idGateway + "-app";
        sensorLorawanDao.deleteSensorInLorawan(applicationId, sensorId);
        log.info("[LoRaWAN] deleted end-device {} in application {}", sensorId, applicationId);
    }

    /* ===== MAPPERS ===== */
    public LorawanSensorData toLorawanCreate(Sensor s) {
        LorawanSensorData d = new LorawanSensorData();
        // Mappe ici ce dont ton API a besoin (ids, join_eui/app_eui/dev_eui, name, attributes, etc.)
        return d;
    }

    /** Construit un payload d'update conforme à LorawanSensorUpdateData (snake_case) */
    public LorawanSensorUpdateData toLorawanUpdate(Sensor s) {
        LorawanSensorUpdateData upd = new LorawanSensorUpdateData();

        LorawanSensorUpdateData.SensorPayload.Ids ids = new LorawanSensorUpdateData.SensorPayload.Ids();
        ids.setSensor_id(s.getIdSensor());

        LorawanSensorUpdateData.SensorPayload payload = new LorawanSensorUpdateData.SensorPayload();
        payload.setIds(ids);

        upd.setSensor(payload);

        // Ajuste selon le field mask attendu par ton endpoint
        upd.setField_mask("ids");

        return upd;
    }
}
