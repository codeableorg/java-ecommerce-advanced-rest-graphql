package com.ecommerce.dto;

/**
 * DTO para transferir datos de producto.
 */
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private Boolean available;
    private Integer sales;

    // Constructor vacío requerido por Jackson
    public ProductDto() {
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public static ProductDto fromEntity(com.ecommerce.model.Product product) {
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

    public static com.ecommerce.model.Product toEntity(ProductDto dto) {
        com.ecommerce.model.Product product = new com.ecommerce.model.Product();
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
