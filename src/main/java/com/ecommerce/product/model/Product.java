package com.ecommerce.product.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entidad de producto para el sistema de ecommerce.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("products")
public class Product {
  @Id
  private Long id;
  private String name;
  private String description;
  private Double price;
  private String category;
  // Removed: available (will be derived from Inventory service)
  // Removed: sales (will be calculated from OrderItems)
}
