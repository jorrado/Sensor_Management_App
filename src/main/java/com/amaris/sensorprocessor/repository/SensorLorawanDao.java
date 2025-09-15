package com.amaris.sensorprocessor.repository;

import com.amaris.sensorprocessor.entity.LorawanSensorData;
import com.amaris.sensorprocessor.entity.LorawanSensorUpdateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

@Repository
public class SensorLorawanDao {

    @Value("${lorawan.baseurlCreate}")
    private String lorawanBaseUrlCreate;

    @Value("${lorawan.baseurl1}")
    private String lorawanBaseUrl;

    @Value("${lorawan.token}")
    private String lorawanToken;

    private final WebClient.Builder webClientBuilder;

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer";

    @Autowired
    public SensorLorawanDao(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /* CREATE */
    public String insertSensorInLorawan(LorawanSensorData lorawanSensorData) {
        return webClientBuilder.build()
                .post()
                .uri(lorawanBaseUrlCreate)
                .header(AUTHORIZATION, BEARER + " " + lorawanToken)
                .bodyValue(lorawanSensorData)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /* DELETE — nouvelle signature avec applicationId */
    public void deleteSensorInLorawan(String applicationId, String sensorId) {
        WebClient client = webClientBuilder
                .baseUrl(lorawanBaseUrl)
                .build();

        client.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/applications/{app}/devices/{dev}")
                        .build(applicationId, sensorId))
                .header(AUTHORIZATION, BEARER + " " + lorawanToken)
                .retrieve()
                .toBodilessEntity()
                .block();
    }


    /* UPDATE — inchangé si ton endpoint reste /{sensorId}. Adapter si besoin. */
    public void updateSensorInLorawan(LorawanSensorUpdateData updateData, String sensorId) {
        webClientBuilder.build()
                .put()
                .uri(lorawanBaseUrl + "/" + sensorId)
                .header(AUTHORIZATION, BEARER + " " + lorawanToken)
                .bodyValue(updateData)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

}
