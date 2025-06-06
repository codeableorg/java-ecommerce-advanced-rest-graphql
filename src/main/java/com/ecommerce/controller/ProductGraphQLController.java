package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador GraphQL para productos.
 */
@Controller
public class ProductGraphQLController {
    @Autowired
    private ProductService productService;

    @QueryMapping
    public Mono<Product> productById(@Argument Long id) {
        return productService.getProductById(id);
    }

    @QueryMapping
    public Flux<Product> productsByCategory(@Argument String category) {
        // Implementar búsqueda por categoría
        return productService.getProductsByCategory(category);
    }

    @QueryMapping
    public Flux<Product> products() {
        return productService.getAllProducts();
    }

    @MutationMapping
    public Mono<Product> createProduct(@Argument("input") ProductInput input) {
        Product product = new Product();
        product.setName(input.getName());
        product.setDescription(input.getDescription());
        product.setPrice(input.getPrice());
        product.setCategory(input.getCategory());
        product.setAvailable(input.getAvailable());
        product.setSales(input.getSales());
        return productService.createProduct(product);
    }

    public static class ProductInput {
        private String name;
        private String description;
        private Double price;
        private String category;
        private Boolean available;
        private Integer sales;
        // Getters y setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Boolean getAvailable() { return available; }
        public void setAvailable(Boolean available) { this.available = available; }
        public Integer getSales() { return sales; }
        public void setSales(Integer sales) { this.sales = sales; }
    }
}
