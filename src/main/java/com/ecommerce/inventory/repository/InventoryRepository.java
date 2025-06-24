package com.ecommerce.inventory.repository;

import com.ecommerce.inventory.model.Inventory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo para inventario.
 */
@Repository
public interface InventoryRepository extends ReactiveCrudRepository<Inventory, Long> {
    Mono<Inventory> findByProductId(Long productId);
}
