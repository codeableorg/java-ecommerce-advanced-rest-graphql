package com.ecommerce.order.controller;

import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.service.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Controlador GraphQL para órdenes.
 */
@Controller
@RequiredArgsConstructor
public class OrderGraphQLController {
  private final OrderService orderService;

  @QueryMapping
  public Mono<Order> orderById(@Argument Long id) {
    return orderService.getOrderById(id);
  }

  @QueryMapping
  public Flux<Order> ordersByUserId(@Argument Long userId) {
    return orderService.getOrdersByUserId(userId);
  }

  @QueryMapping
  public Flux<Order> orders() {
    return orderService.getAllOrders();
  }

  @MutationMapping
  public Mono<Order> createOrder(@Argument("input") OrderInput input) {
    Order order = new Order();
    order.setUserId(input.getUserId());
    order.setStatus(input.getStatus() != null ? input.getStatus() : OrderStatus.PENDING);
    order.setTotalAmount(input.getTotalAmount());
    order.setOrderDate(LocalDateTime.now());
    return orderService.createOrder(order);
  }

  @Data
  public static class OrderInput {
    private Long userId;
    private OrderStatus status;
    private Double totalAmount;
  }
}
