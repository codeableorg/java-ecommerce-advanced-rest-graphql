package com.ecommerce.inventory.client;

import com.ecommerce.inventory.dto.InventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * Cliente HTTP para comunicarse con el Inventory Service a través del API
 * Gateway.
 */
@Component
@RequiredArgsConstructor
public class InventoryClient {

  private final WebClient webClient;

  @Value("${microservices.api-gateway.url:http://localhost:8080}")
  private String apiGatewayUrl;

  /**
   * Obtiene el inventario de un producto por su ID.
   */
  public Mono<InventoryDto> getInventoryByProductId(Long productId) {
    return webClient.get()
        .uri(apiGatewayUrl + "/api/v1/inventory/product/{productId}", productId)
        .retrieve()
        .onStatus(HttpStatus.NOT_FOUND::equals,
            response -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No inventory found for product " + productId)))
        .bodyToMono(InventoryDto.class);
  }

  /**
   * Verifica si hay suficiente stock disponible para un producto.
   */
  public Mono<Boolean> checkProductAvailability(Long productId, Integer quantity) {
    return webClient.get()
        .uri(apiGatewayUrl + "/api/v1/inventory/product/{productId}/availability?quantity={quantity}",
            productId, quantity)
        .retrieve()
        .onStatus(HttpStatus.NOT_FOUND::equals,
            response -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Product with ID " + productId + " not found")))
        .bodyToMono(Boolean.class);
  }

  /**
   * Reserva stock para un producto (disminuye la cantidad disponible).
   */
  public Mono<InventoryDto> reserveStock(Long productId, Integer quantity) {
    return webClient.put()
        .uri(apiGatewayUrl + "/api/v1/inventory/product/{productId}/reserve?quantity={quantity}",
            productId, quantity)
        .retrieve()
        .onStatus(HttpStatus.BAD_REQUEST::equals,
            response -> response.bodyToMono(String.class)
                .flatMap(body -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, body))))
        .onStatus(HttpStatus.NOT_FOUND::equals,
            response -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Product with ID " + productId + " not found")))
        .bodyToMono(InventoryDto.class);
  }

  /**
   * Restaura stock para un producto (aumenta la cantidad disponible).
   */
  public Mono<InventoryDto> restoreStock(Long productId, Integer quantity) {
    return webClient.put()
        .uri(apiGatewayUrl + "/api/v1/inventory/product/{productId}/stock?quantity={quantity}",
            productId, quantity)
        .retrieve()
        .onStatus(HttpStatus.NOT_FOUND::equals,
            response -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Product with ID " + productId + " not found")))
        .bodyToMono(InventoryDto.class);
  }
}
