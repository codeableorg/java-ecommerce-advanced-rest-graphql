package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ProductGraphQLController {
    private final ProductService productService;

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

    @Data
    public static class ProductInput {
        private String name;
        private String description;
        private Double price;
        private String category;
        private Boolean available;
        private Integer sales;
    }
}
