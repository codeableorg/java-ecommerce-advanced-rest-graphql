package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderDto;
import com.ecommerce.order.dto.OrderItemDto;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * REST Controller para órdenes.
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @GetMapping
  public Flux<OrderDto> getAllOrders() {
    return orderService.getAllOrders()
        .map(this::convertToDto);
  }

  @GetMapping("/{id}")
  public Mono<OrderDto> getOrderById(@PathVariable Long id) {
    return orderService.getOrderById(id)
        .map(this::convertToDto);
  }

  @GetMapping("/user/{userId}")
  public Flux<OrderDto> getOrdersByUserId(@PathVariable Long userId) {
    return orderService.getOrdersByUserId(userId)
        .map(this::convertToDto);
  }

  @GetMapping("/{orderId}/items")
  public Flux<OrderItemDto> getOrderItems(@PathVariable Long orderId) {
    return orderService.getOrderItems(orderId)
        .map(this::convertToItemDto);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
    Order order = convertToEntity(orderDto);

    // If order has items, create with items and calculate total automatically
    if (orderDto.getItems() != null && !orderDto.getItems().isEmpty()) {
      List<OrderItem> items = orderDto.getItems().stream()
          .map(this::convertToItemEntity)
          .collect(java.util.stream.Collectors.toList());

      return orderService.createOrderWithItems(order, items)
          .map(this::convertToDto);
    } else {
      // Create empty order (total will be 0.0)
      return orderService.createOrder(order)
          .map(this::convertToDto);
    }
  }

  @PostMapping("/{orderId}/items")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<OrderItemDto> addOrderItem(@PathVariable Long orderId, @RequestBody OrderItemDto orderItemDto) {
    OrderItem orderItem = convertToItemEntity(orderItemDto);
    orderItem.setOrderId(orderId);
    return orderService.addOrderItem(orderItem)
        .map(this::convertToItemDto);
  }

  @DeleteMapping("/{orderId}/items/{itemId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> removeOrderItem(@PathVariable Long orderId, @PathVariable Long itemId) {
    return orderService.removeOrderItem(itemId);
  }

  @PutMapping("/{orderId}/recalculate")
  public Mono<OrderDto> recalculateOrderTotal(@PathVariable Long orderId) {
    return orderService.recalculateOrderTotal(orderId)
        .map(this::convertToDto);
  }

  private OrderDto convertToDto(Order order) {
    OrderDto dto = new OrderDto();
    dto.setId(order.getId());
    dto.setUserId(order.getUserId());
    dto.setTotalAmount(order.getTotalAmount());
    dto.setStatus(order.getStatus());
    dto.setOrderDate(order.getOrderDate());
    // Note: items are not loaded here to avoid N+1 queries
    // Use getOrderItems endpoint to fetch items separately
    return dto;
  }

  private Order convertToEntity(OrderDto dto) {
    Order order = new Order();
    order.setId(dto.getId());
    order.setUserId(dto.getUserId());
    order.setTotalAmount(dto.getTotalAmount());
    order.setStatus(dto.getStatus());
    order.setOrderDate(dto.getOrderDate());
    return order;
  }

  private OrderItemDto convertToItemDto(OrderItem orderItem) {
    OrderItemDto dto = new OrderItemDto();
    dto.setId(orderItem.getId());
    dto.setOrderId(orderItem.getOrderId());
    dto.setProductId(orderItem.getProductId());
    dto.setQuantity(orderItem.getQuantity());
    dto.setUnitPrice(orderItem.getUnitPrice());
    return dto;
  }

  private OrderItem convertToItemEntity(OrderItemDto dto) {
    OrderItem orderItem = new OrderItem();
    orderItem.setId(dto.getId());
    orderItem.setOrderId(dto.getOrderId());
    orderItem.setProductId(dto.getProductId());
    orderItem.setQuantity(dto.getQuantity());
    orderItem.setUnitPrice(dto.getUnitPrice());
    return orderItem;
  }
}
