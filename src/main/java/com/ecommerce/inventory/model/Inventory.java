package com.ecommerce.inventory.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entidad de inventario para el sistema de ecommerce.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("inventory")
public class Inventory {
  @Id
  private Long id;
  private Long productId;
  private Integer stockQuantity;
}
