package com.amaris.sensorprocessor.service;

import com.amaris.sensorprocessor.entity.Gateway;
import com.amaris.sensorprocessor.entity.MonitoringGatewayData;
import com.amaris.sensorprocessor.exception.CustomException;
import com.amaris.sensorprocessor.repository.GatewayDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static com.amaris.sensorprocessor.util.LoggerUtil.logDatabaseError;

@Service
public class GatewayService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GatewayDao gatewayDao;
    private final WebClient webClient;

    @Autowired
    public GatewayService(GatewayDao gatewayDao, WebClient webClient) {
        this.gatewayDao = gatewayDao;
        this.webClient = webClient;
    }

    public List<Gateway> getAllGateways() {
        try {
            return gatewayDao.findAllGateways();
        } catch (Exception e) {
            logger.error("Error retrieving the list of gateways", e);
            System.out.println("\u001B[31m" + "Error retrieving the list of gateways : " + e.getMessage() + "\u001B[0m");
            return Collections.emptyList();
        }
    }

    public void saveGatewayInDatabase(Gateway gateway, BindingResult bindingResult) {
        try {
            if (gatewayDao.findGatewayById(gateway.getGatewayId()).isPresent()) {
                logger.error("Gateway ID already exists : {}", gateway.getGatewayId());
                System.out.println("\u001B[31m" + "Gateway ID already exists : " + gateway.getGatewayId() + "\u001B[0m");
                bindingResult.rejectValue("gatewayId", "Invalid.gatewayId", "Gateway ID already exists");
            }
//            gateway.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))); // UNIQUEMENT POUR LES TESTS SANS LORAWAN
            gatewayDao.insertGatewayInDatabase(gateway);
        } catch (Exception e) {
            logger.error("Database problem", e);
            System.out.println("\u001B[31m" + "Database problem : " + e.getMessage() + "\u001B[0m");
            bindingResult.rejectValue("DatabaseProblem", "Invalid.DatabaseProblem", "Database problem");
        }
    }

    public void deleteGatewayInDatabase(String gatewayId, BindingResult bindingResult) {
        try {
            int deleteLigne = gatewayDao.deleteGatewayById(gatewayId);
            if (deleteLigne == 0) {
                logger.error("Deletion failed or Gateway ID not found {}", gatewayId);
                System.out.println("\u001B[31m" + "Deletion failed or Gateway ID not found : " + gatewayId + "\u001B[0m");
                bindingResult.rejectValue("gatewayId", "Gateway ID not found");
            }
        } catch (Exception e) {
            logger.error("Database problem", e);
            System.out.println("\u001B[31m" + "Database problem : " + e.getMessage() + "\u001B[0m");
            bindingResult.rejectValue("DatabaseProblem", "Database problem");
        }
    }

    public Gateway searchGatewayById(String gatewayId) {
        try {
            return gatewayDao.findGatewayById(gatewayId).orElse(null);
        } catch (Exception e) {
            logDatabaseError(e);
            throw new CustomException("database Problem");
        }
    }

    public void updateGatewayInDatabase(Gateway gateway, BindingResult bindingResult) {
        try {
            int rowsUpdated = gatewayDao.updateGatewayInDatabase(gateway);
            if (rowsUpdated == 0) {
                logger.error("Update in Database => Gateway ID not found : {}", gateway.getGatewayId());
                System.out.println("\u001B[31m" + "Update in Database => Gateway ID not found : " + gateway.getGatewayId() + "\u001B[0m");
                bindingResult.rejectValue("gatewayId", "Invalid.gatewayId", "Gateway ID not found");
            }
        } catch (Exception e) {
            logger.error("Database problem", e);
            System.out.println("\u001B[31m" + "Database problem : " + e.getMessage() + "\u001B[0m");
            bindingResult.rejectValue("LorawanProblem", "Invalid.LorawanProblem", "Database problem");
        }
    }

    /**
     * Récupère un flux SSE (Server-Sent Events) contenant les données de monitoring
     * en temps réel d'une gateway spécifique, à partir de son ID et de son adresse IP.
     * La méthode .bodyToFlux(MonitoringGatewayData.class) convertit directement le JSON en objets Java.
     *
     * @param gatewayId  l'identifiant unique de la gateway
     * @param ipAddress  l'adresse IP de la gateway cible
     * @return un Flux de MonitoringGatewayData émis en continu via SSE
     */
    public Flux<MonitoringGatewayData> getMonitoringData(String gatewayId, String ipAddress) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/monitoring/gateway/{id}")
                .queryParam("ip", ipAddress)
                .build(gatewayId))
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(MonitoringGatewayData.class)
            .doOnError(error -> {
                logger.error("Erreur lors de la récupération des données de monitoring", error);
                System.out.println("\u001B[31m" + "Erreur lors de la récupération des données de monitoring : " +
                        error.getMessage() + "\u001B[0m");
            }
        );
    }

    public void stopMonitoring(String gatewayId) {
        webClient.get()
            .uri("/api/monitoring/gateway/stop/{id}", gatewayId)
            .retrieve()
            .toBodilessEntity()
            .doOnSuccess(response -> logger.info("Monitoring stopped for gateway {}", gatewayId))
            .doOnError(error -> logger.error("Erreur lors de l'arrêt du monitoring pour gateway " + gatewayId, error))
            .subscribe();
    }

}
