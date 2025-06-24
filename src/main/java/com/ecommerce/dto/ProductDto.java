package com.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.ecommerce.model.Product;

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
    private Boolean available;
    private Integer sales;

    public static ProductDto fromEntity(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategory(product.getCategory());
        dto.setAvailable(product.getAvailable());
        dto.setSales(product.getSales());
        return dto;
    }

    public static Product toEntity(ProductDto dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setAvailable(dto.getAvailable());
        product.setSales(dto.getSales());
        return product;
    }
}
