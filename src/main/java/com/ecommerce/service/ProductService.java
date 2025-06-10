package com.ecommerce.service;

import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Servicio para la lógica de negocio de productos.
 */
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Flux<Product> getProducts(Pageable pageable) {
        // ReactiveCrudRepository no soporta paginación directamente, así que usamos
        // findAll y paginamos manualmente
        return productRepository.findAll()
                .skip((long) pageable.getPageNumber() * pageable.getPageSize())
                .take(pageable.getPageSize());
    }

    public Mono<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Flux<Product> getTopSelling(int limit) {
        return productRepository.findTopSelling(limit);
    }

    public Flux<Product> getProductsByCategory(String category) {
        // Implementación básica, se puede mejorar con paginación y filtrado
        return productRepository.findByCategory(category, Pageable.unpaged());
    }

    public Mono<Long> countProducts() {
        return productRepository.count();
    }

    public Flux<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Mono<Product> createProduct(Product product) {
        return productRepository.save(product);
    }

    public Mono<Product> updateProductPartial(Long id, Map<String, Object> updates) {
        return productRepository.findById(id).flatMap(product -> {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "name":
                        product.setName((String) value);
                        break;
                    case "description":
                        product.setDescription((String) value);
                        break;
                    case "price":
                        product.setPrice(value instanceof Number ? ((Number) value).doubleValue()
                                : Double.valueOf(value.toString()));
                        break;
                    case "category":
                        product.setCategory((String) value);
                        break;
                    case "available":
                        product.setAvailable((Boolean) value);
                        break;
                    case "sales":
                        product.setSales(value instanceof Number ? ((Number) value).intValue()
                                : Integer.valueOf(value.toString()));
                        break;
                }
            });
            return productRepository.save(product);
        });
    }

    public Mono<Void> deleteProduct(Long id) {
        return productRepository.deleteById(id);
    }

    public Flux<Product> bulkInsert(Flux<Product> products) {
        return productRepository.saveAll(products);
    }

    public Flux<Product> searchProducts(String name, String category, Double minPrice, Double maxPrice, Boolean available, Integer minSales, int page, int size) {
        int offset = (page > 0 ? page - 1 : 0) * size;
        return productRepository.searchProducts(name, category, minPrice, maxPrice, available, minSales, size, offset);
    }

    public Mono<Long> countSearchProducts(String name, String category, Double minPrice, Double maxPrice, Boolean available, Integer minSales) {
        return productRepository.countSearchProducts(name, category, minPrice, maxPrice, available, minSales);
    }

    // Métodos para filtrado y búsqueda pueden agregarse aquí
}
