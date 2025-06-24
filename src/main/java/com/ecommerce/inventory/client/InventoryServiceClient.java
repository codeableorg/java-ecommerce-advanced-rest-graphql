package com.ecommerce.inventory.client;

import com.ecommerce.inventory.dto.InventoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * Cliente HTTP para comunicarse con el Inventory Service.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceClient {

    @Qualifier("inventoryServiceWebClient")
    private final WebClient inventoryWebClient;

    /**
     * Verifica disponibilidad de producto.
     */
    public Mono<Boolean> checkProductAvailability(Long productId, Integer quantity) {
        log.info("Checking availability: productId={}, quantity={}", productId, quantity);
        return inventoryWebClient
                .get()
                .uri("/api/v1/inventory/product/{productId}/availability?quantity={quantity}", productId, quantity)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, 
                    response -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No inventory found for product " + productId)))
                .bodyToMono(Boolean.class)
                .doOnNext(available -> log.info("Availability check result: {}", available))
                .doOnError(error -> log.error("Error checking availability for productId: {}", productId, error));
    }

    /**
     * Reserva stock de producto.
     */
    public Mono<InventoryDto> reserveStock(Long productId, Integer quantity) {
        log.info("Reserving stock: productId={}, quantity={}", productId, quantity);
        return inventoryWebClient
                .put()
                .uri("/api/v1/inventory/product/{productId}/reserve?quantity={quantity}", productId, quantity)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                    response -> response.bodyToMono(String.class)
                        .flatMap(errorMsg -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMsg))))
                .bodyToMono(InventoryDto.class)
                .doOnNext(result -> log.info("Stock reserved successfully: {}", result))
                .doOnError(error -> log.error("Error reserving stock for productId: {}", productId, error));
    }

    /**
     * Restaura stock de producto (devuelve el stock reservado).
     */
    public Mono<InventoryDto> restoreStock(Long productId, Integer quantity) {
        log.info("Restoring stock: productId={}, quantity={}", productId, quantity);
        return inventoryWebClient
                .put()
                .uri("/api/v1/inventory/product/{productId}/stock?quantity={quantity}", productId, quantity)
                .retrieve()
                .bodyToMono(InventoryDto.class)
                .doOnNext(result -> log.info("Stock restored successfully: {}", result))
                .doOnError(error -> log.error("Error restoring stock for productId: {}", productId, error));
    }
}