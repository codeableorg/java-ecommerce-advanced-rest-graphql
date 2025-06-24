package com.ecommerce.order.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entidad de item de orden para el sistema de ecommerce.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("order_items")
public class OrderItem {
  @Id
  private Long id;
  private Long orderId;
  private Long productId;
  private Integer quantity;
  private Double unitPrice;
}
