package com.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuración de WebClient para comunicación con microservicios.
 */
@Configuration
public class WebClientConfig {

  @Value("${inventory-service.base-url}")
  private String inventoryServiceBaseUrl;

  @Bean("inventoryServiceWebClient")
  public WebClient inventoryServiceWebClient() {
    return WebClient.builder()
        .baseUrl(inventoryServiceBaseUrl)
        .build();
  }
}
