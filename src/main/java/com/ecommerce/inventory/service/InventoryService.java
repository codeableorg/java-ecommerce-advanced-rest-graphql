package com.ecommerce.inventory.service;

import com.ecommerce.inventory.model.Inventory;
import com.ecommerce.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Servicio para la lógica de negocio de inventario.
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public Flux<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Mono<Inventory> getInventoryById(Long id) {
        return inventoryRepository.findById(id);
    }

    public Mono<Inventory> getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId);
    }

    public Mono<Boolean> isProductAvailable(Long productId, Integer requestedQuantity) {
        return getInventoryByProductId(productId)
                .map(inventory -> inventory.getStockQuantity() >= requestedQuantity)
                .defaultIfEmpty(false);
    }

    public Mono<Inventory> updateStock(Long productId, Integer quantity) {
        return getInventoryByProductId(productId)
                .flatMap(inventory -> {
                    inventory.setStockQuantity(inventory.getStockQuantity() + quantity);
                    return inventoryRepository.save(inventory);
                });
    }

    public Mono<Inventory> reserveStock(Long productId, Integer quantity) {
        return getInventoryByProductId(productId)
                .flatMap(inventory -> {
                    if (inventory.getStockQuantity() >= quantity) {
                        inventory.setStockQuantity(inventory.getStockQuantity() - quantity);
                        return inventoryRepository.save(inventory);
                    } else {
                        return Mono.error(new RuntimeException("Insufficient stock"));
                    }
                });
    }
}
