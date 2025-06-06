package com.ecommerce.repository;

import com.ecommerce.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Repositorio reactivo para productos.
 */
@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
    Flux<Product> findByCategory(String category, Pageable pageable);

    Flux<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Flux<Product> findByAvailable(boolean available, Pageable pageable);

    @Query("SELECT * FROM products WHERE sales > 0 ORDER BY sales DESC LIMIT :limit")
    Flux<Product> findTopSelling(int limit);

    // Métodos para filtrado avanzado pueden agregarse aquí
}
