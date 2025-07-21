package com.amaris.sensorprocessor.repository;

import com.amaris.sensorprocessor.entity.LorawanGatewayData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Repository
public class GatewayLorawanDao {

    @Value("${lorawan.baseurlCreate}")
    private String lorawanBaseUrlCreate;

    @Value("${lorawan.baseurlDelete}")
    private String lorawanBaseUrlDelete;

    @Value("${lorawan.token}")
    private String lorawanToken;

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public GatewayLorawanDao(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public String insertGatewayInLorawan(LorawanGatewayData lorawanGatewayData) {
        return webClientBuilder.build()
            .post()
            .uri(lorawanBaseUrlCreate)
            .header("Authorization", "Bearer " + lorawanToken)
            .bodyValue(lorawanGatewayData)
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }

    public int deleteGatewayById(String gatewayId) {
        return webClientBuilder.build().delete()
            .uri(lorawanBaseUrlDelete + "/" + gatewayId)
            .header("Authorization", "Bearer " + lorawanToken)
            .retrieve()
            .toBodilessEntity()
            .map(response -> response.getStatusCode().value())
            .blockOptional()
            .orElse(-1);
    }

}
