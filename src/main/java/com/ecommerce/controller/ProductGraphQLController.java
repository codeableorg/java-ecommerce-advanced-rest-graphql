package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
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
}
