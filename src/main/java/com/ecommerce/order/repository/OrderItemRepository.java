package com.ecommerce.order.repository;

import com.ecommerce.order.model.OrderItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Repositorio reactivo para items de orden.
 */
@Repository
public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> {
    Flux<OrderItem> findByOrderId(Long orderId);
    Flux<OrderItem> findByProductId(Long productId);
}
