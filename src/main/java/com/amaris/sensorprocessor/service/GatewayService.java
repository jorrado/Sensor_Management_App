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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
            logger.error("Erreur lors de la récupération de la liste des gateways", e);
            System.out.println("\u001B[31m" + "Erreur lors de la récupération de la liste des gateways : " +
                    e.getMessage() + "\u001B[0m");
//            throw new CustomException("Database problem");
            return Collections.emptyList();
        }
    }

    public void save(Gateway gateway) {
        try {
            if (gatewayDao.findGatewayById(gateway.getGatewayId()).isPresent()) {
                throw new CustomException("Gateway already exists");
            }
            gateway.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));// A SUPPRIMER UNIQUEMENT POUR LE TEST
            gatewayDao.insertGateway(gateway);
        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde de la gateway", e);
            System.out.println("\u001B[31m" + "Erreur lors de la sauvegarde de la gateway : " +
                    e.getMessage() + "\u001B[0m");
//            throw new CustomException("Database problem");
        }
    }

    public void deleteGateway(String gatewayId) {
        try {
            gatewayDao.deleteGatewayById(gatewayId);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la gateway", e);
            System.out.println("\u001B[31m" + "Erreur lors de la suppression de la gateway : " +
                    e.getMessage() + "\u001B[0m");
//            throw new CustomException("Database problem");
        }
    }

    public Gateway searchGatewayById(String gatewayId) {
        try {
            Optional<Gateway> gateway = gatewayDao.findGatewayById(gatewayId);
            if (gateway.isEmpty()) {
                throw new CustomException("Gateway don't exists");
            }
            return gateway.get();
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de la gateway par ID", e);
            System.out.println("\u001B[31m" + "Erreur lors de la recherche de la gateway : " +
                    e.getMessage() + "\u001B[0m");
//            throw new CustomException("Database problem");
            return null;
        }
    }

    public void update(Gateway gateway) {
        try {
            gatewayDao.updateGateway(gateway);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la gateway", e);
            System.out.println("\u001B[31m" + "Erreur lors de la mise à jour de la gateway : " +
                    e.getMessage() + "\u001B[0m");
//            throw new CustomException("Database problem");
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

}
