package com.ecommerce.repository;

import com.ecommerce.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Repository;

/**
 * Repositorio reactivo para productos.
 */
@Repository
public interface ProductRepository extends org.springframework.data.repository.reactive.ReactiveCrudRepository<Product, Long> {
    Flux<Product> findByCategory(String category, Pageable pageable);

    Flux<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Flux<Product> findByAvailable(boolean available, Pageable pageable);

    @Query("SELECT * FROM products WHERE sales > 0 ORDER BY sales DESC LIMIT :limit")
    Flux<Product> findTopSelling(int limit);

    @Query("""
        SELECT * FROM products
        WHERE (:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:category IS NULL OR category = :category)
          AND (:minPrice IS NULL OR price >= :minPrice)
          AND (:maxPrice IS NULL OR price <= :maxPrice)
          AND (:available IS NULL OR available = :available)
          AND (:minSales IS NULL OR sales >= :minSales)
        ORDER BY id ASC
        LIMIT :limit OFFSET :offset
    """)
    Flux<Product> searchProducts(
        @Param("name") String name,
        @Param("category") String category,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("available") Boolean available,
        @Param("minSales") Integer minSales,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    @Query("""
        SELECT COUNT(*) FROM products
        WHERE (:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:category IS NULL OR category = :category)
          AND (:minPrice IS NULL OR price >= :minPrice)
          AND (:maxPrice IS NULL OR price <= :maxPrice)
          AND (:available IS NULL OR available = :available)
          AND (:minSales IS NULL OR sales >= :minSales)
    """)
    Mono<Long> countSearchProducts(
        @Param("name") String name,
        @Param("category") String category,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("available") Boolean available,
        @Param("minSales") Integer minSales
    );

    // Métodos para filtrado avanzado pueden agregarse aquí
}
