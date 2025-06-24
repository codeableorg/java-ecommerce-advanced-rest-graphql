package com.ecommerce.product.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.ecommerce.product.model.Product;

/**
 * DTO para transferir datos de producto.
 */
@Data
@NoArgsConstructor
public class ProductDto {
  private Long id;
  private String name;
  private String description;
  private Double price;
  private String category;
  // Removed: available (will be derived from Inventory service)
  // Removed: sales (will be calculated from OrderItems)

  public static ProductDto fromEntity(Product product) {
    ProductDto dto = new ProductDto();
    dto.setId(product.getId());
    dto.setName(product.getName());
    dto.setDescription(product.getDescription());
    dto.setPrice(product.getPrice());
    dto.setCategory(product.getCategory());
    return dto;
  }

  public static Product toEntity(ProductDto dto) {
    Product product = new Product();
    product.setId(dto.getId());
    product.setName(dto.getName());
    product.setDescription(dto.getDescription());
    product.setPrice(dto.getPrice());
    product.setCategory(dto.getCategory());
    return product;
  }
}
