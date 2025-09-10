package com.amaris.sensorprocessor.service;

import com.amaris.sensorprocessor.repository.SensorDao;
import com.amaris.sensorprocessor.entity.Sensor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SensorService {

    private final SensorDao sensorDao;

    public SensorService(SensorDao sensorDao) {
        this.sensorDao = sensorDao;
    }

    @Transactional(readOnly = true)
    public List<Sensor> getAllSensors() {
        return sensorDao.findAllSensors();
    }
}
