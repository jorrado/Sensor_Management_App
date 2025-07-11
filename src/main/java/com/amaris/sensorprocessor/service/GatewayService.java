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
            return Collections.emptyList();
        }
    }

    public int save(Gateway gateway) {
        try {
            if (gatewayDao.findByIdOfGateway(gateway.getIdGateway()).isPresent()) {
                throw new CustomException("Gateway already exists");
            }
            return gatewayDao.insertGateway(gateway);
        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde de la gateway", e);
            System.out.println("\u001B[31m" + "Erreur lors de la sauvegarde de la gateway : " +
                    e.getMessage() + "\u001B[0m");
            throw new CustomException("Database problem");
        }
    }

    public int deleteGateway(String idGateway) {
        try {
            return gatewayDao.deleteByIdOfGateway(idGateway);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la gateway", e);
            System.out.println("\u001B[31m" + "Erreur lors de la suppression de la gateway : " +
                    e.getMessage() + "\u001B[0m");
            throw new CustomException("Database problem");
        }
    }

    public Gateway searchGatewayById(String idGateway) {
        try {
            Optional<Gateway> gateway = gatewayDao.findByIdOfGateway(idGateway);
            if (gateway.isEmpty()) {
                throw new CustomException("Gateway don't exists");
            }
            return gateway.get();
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de la gateway par ID", e);
            System.out.println("\u001B[31m" + "Erreur lors de la recherche de la gateway : " +
                    e.getMessage() + "\u001B[0m");
            throw new CustomException("Database problem");
        }
    }

    public int update(Gateway gateway) {
        try {
            return gatewayDao.updateGateway(gateway);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la gateway", e);
            System.out.println("\u001B[31m" + "Erreur lors de la mise à jour de la gateway : " +
                    e.getMessage() + "\u001B[0m");
            throw new CustomException("Database problem");
        }
    }

    /**
     * Récupère un flux de données de monitoring pour une gateway donnée via SSE.
     * Accepte un id de gateway et une IP, renvoie un Flux d'objets MonitoringGatewayData.
     * La méthode .bodyToFlux(MonitoringGatewayData.class) convertit directement le JSON en objets Java.
     * Ne plante pas si la réponse est vide ou nulle.
     *
     * @param id Identifiant de la gateway
     * @param ip Adresse IP de la gateway
     * @return Flux de données MonitoringGatewayData en SSE
     */
    public Flux<MonitoringGatewayData> getMonitoringData(String id, String ip) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/monitoring/gateway/{id}")
                        .queryParam("ip", ip)
                        .build(id))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(MonitoringGatewayData.class)
                .doOnError(error -> {
                            logger.error("Erreur lors de la récupération des données de monitoring", error);
                            System.out.println("\u001B[31m" + "Erreur lors de la récupération des données de monitoring : " +
                                    error.getMessage() + "\u001B[0m");
                        });
    }

}
