package com.ecommerce.order.repository;

import com.ecommerce.order.model.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Repositorio reactivo para órdenes.
 */
@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
    Flux<Order> findByUserId(Long userId);
}
