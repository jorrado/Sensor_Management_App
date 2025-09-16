package com.amaris.sensorprocessor.repository;

import com.amaris.sensorprocessor.entity.LorawanSensorData;
import com.amaris.sensorprocessor.entity.LorawanSensorUpdateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Repository
public class SensorLorawanDao {

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

    /* CREATE — POST /applications/{app}/devices */
    public void insertSensorInLorawan(String applicationId, LorawanSensorData body) {
        WebClient client = webClientBuilder.baseUrl(lorawanBaseUrl).build();

        client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/{app}/devices")
                        .build(applicationId))
                .header(AUTHORIZATION, BEARER + " " + lorawanToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        resp -> resp.bodyToMono(String.class).flatMap(msg -> {
                            System.err.println("[TTN] Create device error body: " + msg);
                            return Mono.error(new WebClientResponseException(
                                    "TTN error: " + msg,
                                    resp.statusCode().value(), resp.statusCode().toString(),
                                    null, null, null));
                        })
                )
                .toBodilessEntity()
                .block();
    }

    /* UPDATE — PATCH /applications/{app}/devices/{dev} */
    public void updateSensorInLorawan(String applicationId, String sensorId, LorawanSensorUpdateData body) {
        WebClient client = webClientBuilder.baseUrl(lorawanBaseUrl).build();

        client.method(HttpMethod.PATCH)
                .uri(uriBuilder -> uriBuilder
                        .path("/{app}/devices/{dev}")
                        .build(applicationId, sensorId))
                .header(AUTHORIZATION, BEARER + " " + lorawanToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        resp -> resp.bodyToMono(String.class).flatMap(msg -> {
                            System.err.println("[TTN] Update device error body: " + msg);
                            return Mono.error(new WebClientResponseException(
                                    "TTN error: " + msg,
                                    resp.statusCode().value(), resp.statusCode().toString(),
                                    null, null, null));
                        })
                )
                .toBodilessEntity()
                .block();
    }

    /* DELETE — DELETE /applications/{app}/devices/{dev} */
    public void deleteSensorInLorawan(String applicationId, String sensorId) {
        WebClient client = webClientBuilder.baseUrl(lorawanBaseUrl).build();

        client.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/{app}/devices/{dev}")
                        .build(applicationId, sensorId))
                .header(AUTHORIZATION, BEARER + " " + lorawanToken)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        resp -> resp.bodyToMono(String.class).flatMap(msg -> {
                            System.err.println("[TTN] Delete device error body: " + msg);
                            return Mono.error(new WebClientResponseException(
                                    "TTN error: " + msg,
                                    resp.statusCode().value(), resp.statusCode().toString(),
                                    null, null, null));
                        })
                )
                .toBodilessEntity()
                .block();
    }
}
