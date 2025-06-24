package com.ecommerce.order.dto;

import lombok.Data;

/**
 * DTO para transferir datos de OrderItem.
 */
@Data
public class OrderItemDto {
  private Long id;
  private Long orderId;
  private Long productId;
  private Integer quantity;
  private Double unitPrice;
}
