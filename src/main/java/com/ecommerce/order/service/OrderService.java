package com.ecommerce.order.service;

import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.repository.OrderItemRepository;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la lógica de negocio de órdenes.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final ProductRepository productRepository;
  private final InventoryRepository inventoryRepository;

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
    // Set initial values for new order
    order.setTotalAmount(0.0); // Will be calculated from items
    order.setOrderDate(LocalDateTime.now());
    return orderRepository.save(order);
  }

  public Mono<OrderItem> addOrderItem(OrderItem orderItem) {
    return validateProductAndStock(orderItem.getProductId(), orderItem.getQuantity())
        .then(reserveInventory(orderItem.getProductId(), orderItem.getQuantity()))
        .then(orderItemRepository.save(orderItem))
        .flatMap(savedItem -> recalculateOrderTotal(savedItem.getOrderId())
            .thenReturn(savedItem))
        .onErrorResume(error -> {
          // If save fails after inventory reservation, restore inventory
          return restoreInventory(orderItem.getProductId(), orderItem.getQuantity())
              .then(Mono.error(error));
        });
  }

  public Mono<Void> removeOrderItem(Long orderItemId) {
    return orderItemRepository.findById(orderItemId)
        .flatMap(orderItem -> {
          Long orderId = orderItem.getOrderId();
          // Return inventory when removing item
          return restoreInventory(orderItem.getProductId(), orderItem.getQuantity())
              .then(orderItemRepository.deleteById(orderItemId))
              .then(recalculateOrderTotal(orderId))
              .then();
        });
  }

  private Mono<Void> restoreInventory(Long productId, Integer quantity) {
    return inventoryRepository.findByProductId(productId)
        .flatMap(inventory -> {
          inventory.setStockQuantity(inventory.getStockQuantity() + quantity);
          return inventoryRepository.save(inventory).then();
        });
  }

  public Mono<Order> recalculateOrderTotal(Long orderId) {
    return orderItemRepository.findByOrderId(orderId)
        .map(item -> item.getQuantity() * item.getUnitPrice())
        .reduce(0.0, Double::sum)
        .flatMap(total -> orderRepository.findById(orderId)
            .flatMap(order -> {
              order.setTotalAmount(total);
              return orderRepository.save(order);
            }));
  }

  public Mono<Order> createOrderWithItems(Order order, List<OrderItem> items) {
    // Step 1: Validate all products exist and have sufficient stock (but don't
    // reserve yet)
    return Flux.fromIterable(items)
        .flatMap(item -> validateProductAndStock(item.getProductId(), item.getQuantity()))
        .then(
            // Step 2: Reserve inventory for all items
            Flux.fromIterable(items)
                .flatMap(item -> reserveInventory(item.getProductId(), item.getQuantity()))
                .then(createOrder(order))
                .flatMap(savedOrder -> {
                  // Set order ID for all items and calculate total
                  double total = items.stream()
                      .mapToDouble(item -> {
                        item.setOrderId(savedOrder.getId());
                        return item.getQuantity() * item.getUnitPrice();
                      })
                      .sum();

                  // Save all items
                  return Flux.fromIterable(items)
                      .flatMap(orderItemRepository::save)
                      .collectList()
                      .then(Mono.defer(() -> {
                        // Update order with calculated total
                        savedOrder.setTotalAmount(total);
                        return orderRepository.save(savedOrder);
                      }));
                })
                .onErrorResume(error -> {
                  // If anything fails after inventory reservation, restore inventory
                  return Flux.fromIterable(items)
                      .flatMap(item -> restoreInventory(item.getProductId(), item.getQuantity())
                          .onErrorContinue((e, obj) -> {
                            System.err.println("Failed to restore inventory for product " + item.getProductId() + ": "
                                + e.getMessage());
                          }))
                      .then(Mono.error(error));
                }));
  }

  private Mono<Void> validateProductAndStock(Long productId, Integer quantity) {
    return productRepository.findById(productId)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Product with ID " + productId + " not found")))
        .then(inventoryRepository.findByProductId(productId)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No inventory found for product " + productId)))
            .flatMap(inventory -> {
              if (inventory.getStockQuantity() < quantity) {
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Insufficient stock. Available: " + inventory.getStockQuantity() + ", Requested: " + quantity));
              }
              return Mono.empty();
            }));
  }

  private Mono<Void> reserveInventory(Long productId, Integer quantity) {
    return inventoryRepository.findByProductId(productId)
        .flatMap(inventory -> {
          inventory.setStockQuantity(inventory.getStockQuantity() - quantity);
          return inventoryRepository.save(inventory).then();
        });
  }
}
