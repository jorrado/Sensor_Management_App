package com.amaris.sensorprocessor.service;

import com.amaris.sensorprocessor.entity.Gateway;
import com.amaris.sensorprocessor.exception.CustomException;
import com.amaris.sensorprocessor.repository.GatewayDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Service
public class GatewayService {

    private final GatewayDao gatewayDao;

    @Autowired
    private RestClient restClient;

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

    public String getMonitoringData(String id) {
        return restClient.get()
                .uri("http://localhost:8081/api/monitoring/{id}", id) // à utiliser sans le conteneur
                // .uri("http://appli2:8081/api/monitoring/{id}", id) // à utiliser avec le nom du conteneur ici appli2
                .retrieve()
                .body(String.class);
    }

}
