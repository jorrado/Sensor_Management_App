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

    private static final String CLUSTER = "eu1.cloud.thethings.network";
    private static final String LORAWAN_VERSION = "1.0.3";
    private static final String LORAWAN_PHY_VERSION = "1.0.3-a";

    /* ================= CRUD TTN ================= */

    public void createDevice(String idGateway, LorawanSensorData body) {
        String applicationId = idGateway + "-appli";
        sensorLorawanDao.insertSensorInLorawan(applicationId, body);
        log.info("[LoRaWAN] created end-device in application {}", applicationId);
    }

    public void updateDevice(String idGateway, String sensorId, LorawanSensorUpdateData body) {
        String applicationId = idGateway + "-appli";
        sensorLorawanDao.updateSensorInLorawan(applicationId, sensorId, body);
        log.info("[LoRaWAN] updated end-device {} in application {}", sensorId, applicationId);
    }

    public void deleteDevice(String idGateway, String sensorId) {
        String applicationId = idGateway + "-appli";
        sensorLorawanDao.deleteSensorInLorawan(applicationId, sensorId);
        log.info("[LoRaWAN] deleted end-device {} in application {}", sensorId, applicationId);
    }

    /* ================= MAPPERS ================= */

    /**
     * Construit le body OTAA pour TTN en utilisant exactement les valeurs du formulaire
     * (deviceId/devEui/joinEui/appKey).
     */
    public LorawanSensorData toLorawanCreate(Sensor s) {
        if (isBlank(s.getIdSensor())) throw new IllegalArgumentException("idSensor is required");
        if (isBlank(s.getDevEui()))   throw new IllegalArgumentException("devEui is required");
        if (isBlank(s.getJoinEui()))  throw new IllegalArgumentException("joinEui is required");
        if (isBlank(s.getAppKey()))   throw new IllegalArgumentException("appKey is required");

        // ids
        LorawanSensorData.Ids ids = new LorawanSensorData.Ids();
        ids.setDeviceId(s.getIdSensor());  // ⚠️ TTN attend lowercase [a-z0-9-] → à valider côté form
        ids.setDevEui(s.getDevEui());      // ⚠️ 16 hex → à valider côté form
        ids.setJoinEui(s.getJoinEui());    // ⚠️ 16 hex → à valider côté form

        // root_keys
        LorawanSensorData.Key appKeyObj = new LorawanSensorData.Key();
        appKeyObj.setKey(s.getAppKey());   // ⚠️ 32 hex → à valider côté form

        LorawanSensorData.RootKeys rootKeys = new LorawanSensorData.RootKeys();
        rootKeys.setAppKey(appKeyObj);

        // end_device
        LorawanSensorData.EndDevice ed = new LorawanSensorData.EndDevice();
        ed.setIds(ids);
        ed.setSupportsJoin(true);
        ed.setNetworkServerAddress(CLUSTER);
        ed.setApplicationServerAddress(CLUSTER);
        ed.setJoinServerAddress(CLUSTER);
        ed.setLorawanVersion(LORAWAN_VERSION);
        ed.setLorawanPhyVersion(LORAWAN_PHY_VERSION);
        ed.setRootKeys(rootKeys);
        ed.setName(s.getIdSensor()); // optionnel

        LorawanSensorData dto = new LorawanSensorData();
        dto.setEndDevice(ed);
        return dto;
    }

    /** UPDATE **/
    public LorawanSensorUpdateData toLorawanUpdate(Sensor s) {
        LorawanSensorUpdateData upd = new LorawanSensorUpdateData();

        LorawanSensorUpdateData.EndDevice ed = new LorawanSensorUpdateData.EndDevice();
        ed.setName(!isBlank(s.getDeviceType()) ? s.getDeviceType() : s.getIdSensor());
        upd.setEndDevice(ed);

        LorawanSensorUpdateData.FieldMask fm = new LorawanSensorUpdateData.FieldMask();
        fm.setPaths(java.util.List.of("name"));
        upd.setFieldMask(fm);

        return upd;
    }

    /* ================= Utils ================= */

    private static boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }
}
