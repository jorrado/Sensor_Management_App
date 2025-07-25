package com.amaris.sensorprocessor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${api.base.url}")
    private String baseUrl;

    /**
     * Configure et crée un bean WebClient réutilisable avec une URL de base définie
     * dans les propriétés de l’application.
     * Pour lancer une communication avec une API REST conteneurisée il faut modifier
     * la variable baseUrl dans le fichier properties
     *
     * @return une instance de WebClient configurée avec la baseUrl spécifiée
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

}
