package com.ecommerce.order.service;

import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Servicio para la lógica de negocio de órdenes.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Mono<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Flux<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Flux<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public Mono<Order> createOrder(Order order) {
        return orderRepository.save(order);
    }

    public Mono<OrderItem> addOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    // Method that shows coupling - needs to access Product service for top-selling calculation
    public Flux<OrderItem> getTopSellingProducts(int limit) {
        // This will demonstrate inter-service dependencies in workshop
        return orderItemRepository.findAll()
                .groupBy(OrderItem::getProductId)
                .flatMap(group -> group.reduce((item1, item2) -> {
                    item1.setQuantity(item1.getQuantity() + item2.getQuantity());
                    return item1;
                }))
                .sort((item1, item2) -> item2.getQuantity().compareTo(item1.getQuantity()))
                .take(limit);
    }
}
