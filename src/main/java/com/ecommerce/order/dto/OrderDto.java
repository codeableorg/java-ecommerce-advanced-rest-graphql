package com.ecommerce.order.dto;

import com.ecommerce.order.model.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para transferir datos de Order.
 */
@Data
public class OrderDto {
  private Long id;
  private Long userId;
  private Double totalAmount;
  private OrderStatus status;
  private LocalDateTime orderDate;
  private List<OrderItemDto> items;
}
