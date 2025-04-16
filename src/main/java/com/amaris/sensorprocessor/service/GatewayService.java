package com.amaris.sensorprocessor.service;

import com.amaris.sensorprocessor.entity.Gateway;
import com.amaris.sensorprocessor.entity.User;
import com.amaris.sensorprocessor.exception.CustomException;
import com.amaris.sensorprocessor.repository.GatewayDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GatewayService {

    private final GatewayDao gatewayDao;

    @Autowired
    public GatewayService(GatewayDao gatewayDao) {
        this.gatewayDao = gatewayDao;
    }

    public List<Gateway> getAllGateways() {
        return gatewayDao.findAllGateways();
    }

    public int save(Gateway gateway) {
        if (!gatewayDao.findByIdOfGateway(gateway.getIdGateway()).isEmpty()) {
            throw new CustomException("Gateway already exists");
        }
        return gatewayDao.insertGateway(gateway);
    }

    public int deleteGateway(String idGateway) {
        return gatewayDao.deleteByIdOfGateway(idGateway);
    }

    public Gateway searchGatewayById(String idGateway) {
        Optional<Gateway> gateway = gatewayDao.findByIdOfGateway(idGateway);
        if (gateway.isEmpty()) {
            throw new CustomException("Gateway don't exists");
        }
        return gateway.get();
    }

    public int update(Gateway gateway) {
        return gatewayDao.updateGateway(gateway);
    }

}
