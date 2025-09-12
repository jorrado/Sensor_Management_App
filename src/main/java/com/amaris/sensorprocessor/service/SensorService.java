package com.amaris.sensorprocessor.service;

import com.amaris.sensorprocessor.entity.LorawanSensorData;
import com.amaris.sensorprocessor.entity.LorawanSensorUpdateData;
import com.amaris.sensorprocessor.entity.Sensor;
import com.amaris.sensorprocessor.repository.SensorDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorDao sensorDao;                 // DAO JdbcTemplate
    private final SensorLorawanService lorawanService; // Intégration TTN

    /* READ */
    public List<Sensor> findAll() { return sensorDao.findAllSensors(); }
    public Optional<Sensor> findByIdSensor(String idSensor) { return sensorDao.findByIdOfSensor(idSensor); }
    public Sensor getOrThrow(String idSensor) {
        return findByIdSensor(idSensor).orElseThrow(() -> new IllegalArgumentException("Sensor not found: " + idSensor));
    }

    /* CREATE */
    @Transactional
    public Sensor create(Sensor toCreate) {
        if (toCreate.getIdSensor() == null || toCreate.getIdSensor().isBlank())
            throw new IllegalArgumentException("idSensor is required");

        if (sensorDao.findByIdOfSensor(toCreate.getIdSensor()).isPresent())
            throw new IllegalStateException("idSensor already exists: " + toCreate.getIdSensor());

        if (toCreate.getCommissioningDate() == null)
            toCreate.setCommissioningDate(String.valueOf(Instant.now()));

        if (toCreate.getStatus() == null) toCreate.setStatus(Boolean.TRUE);

        int rows = sensorDao.insertSensor(toCreate);
        if (rows != 1) throw new IllegalStateException("DB insert failed for sensor " + toCreate.getIdSensor());
        log.info("[Sensor] DB created idSensor={}", toCreate.getIdSensor());

        LorawanSensorData lorawan = lorawanService.toLorawanCreate(toCreate);
        lorawanService.createDevice(lorawan);
        log.info("[Sensor] TTN created device {}", toCreate.getIdSensor());

        return sensorDao.findByIdOfSensor(toCreate.getIdSensor()).orElse(toCreate);
    }

    /* UPDATE (sans rename d'idSensor) */
    @Transactional
    public Sensor update(String idSensor, Sensor patch) {
        Sensor existing = getOrThrow(idSensor);

        if (patch.getIdSensor() != null && !patch.getIdSensor().isBlank()
                && !patch.getIdSensor().equals(existing.getIdSensor())) {
            throw new IllegalArgumentException("Renaming idSensor is not supported by current DAO");
        }

        if (patch.getDeviceType() != null) existing.setDeviceType(patch.getDeviceType());
        if (patch.getCommissioningDate() != null) existing.setCommissioningDate(patch.getCommissioningDate());
        if (patch.getStatus() != null) existing.setStatus(patch.getStatus());
        if (patch.getBuildingName() != null) existing.setBuildingName(patch.getBuildingName());
        if (patch.getFloor() != null) existing.setFloor(patch.getFloor());
        if (patch.getLocation() != null) existing.setLocation(patch.getLocation());
        if (patch.getIdGateway() != null) existing.setIdGateway(patch.getIdGateway());

        int rows = sensorDao.updateSensor(existing);
        if (rows != 1) throw new IllegalStateException("DB update failed for sensor " + idSensor);
        log.info("[Sensor] DB updated idSensor={}", idSensor);

        // TTN update
        LorawanSensorUpdateData updateDto = lorawanService.toLorawanUpdate(existing);
        lorawanService.updateDevice(idSensor, updateDto);
        log.info("[Sensor] TTN updated device {}", idSensor);

        return existing;
    }

    /* DELETE — appelle TTN avec application = {idGateway}-app puis supprime en DB */
    @Transactional
    public void delete(String idSensor) {
        // 0) On vérifie qu'il existe en BDD (sinon rien à faire)
        Sensor existing = getOrThrow(idSensor);

        // 1) Tentative de suppression TTN — on N'EMPÊCHE PAS la suite si ça échoue
        try {
            lorawanService.deleteDevice(existing.getIdGateway(), idSensor);
            log.info("[Sensor] TTN deleted device {} (app={}-app)", idSensor, existing.getIdGateway());
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                // Déjà supprimé côté TTN : OK, on continue
                log.warn("[Sensor] TTN device {} not found in app {}-app (already deleted?)",
                        idSensor, existing.getIdGateway());
            } else {
                // Autre erreur TTN : on log et on CONTINUE la suppression BDD
                log.error("[Sensor] TTN delete failed for {} (app={}-app): {}",
                        idSensor, existing.getIdGateway(), e.getMessage());
            }
        } catch (Exception e) {
            // Toute autre erreur réseau/runtime → on continue quand même
            log.error("[Sensor] TTN delete unexpected error for {}: {}", idSensor, e.getMessage(), e);
        }

        // 2) Suppression en base — toujours exécutée
        int rows = sensorDao.deleteByIdOfSensor(idSensor);
        if (rows == 0) throw new IllegalArgumentException("Sensor not found: " + idSensor);

        log.info("[Sensor] DB deleted idSensor={}", idSensor);
    }


    /* SET STATUS */
    @Transactional
    public Sensor setStatus(String idSensor, boolean active) {
        Sensor existing = getOrThrow(idSensor);
        existing.setStatus(active);

        int rows = sensorDao.updateSensor(existing);
        if (rows != 1) throw new IllegalStateException("DB update status failed for " + idSensor);

        return existing;
    }
}
