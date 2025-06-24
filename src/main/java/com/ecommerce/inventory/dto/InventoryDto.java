package com.ecommerce.inventory.dto;

import lombok.Data;

/**
 * DTO para transferir datos de Inventory.
 */
@Data
public class InventoryDto {
  private Long id;
  private Long productId;
  private Integer stockQuantity;
}
