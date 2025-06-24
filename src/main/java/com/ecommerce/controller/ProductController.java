package com.ecommerce.controller;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Controlador REST para productos.
 */
@RestController
@RequestMapping(value = "/api/v1/products", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Mono<Map<String, Object>> getProducts(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        int pageIndex = page > 0 ? page - 1 : 0;
        return productService.getProducts(PageRequest.of(pageIndex, size))
                .collectList()
                .zipWith(productService.countProducts())
                .map(tuple -> {
                    List<ProductDto> products = tuple.getT1().stream().map(ProductDto::fromEntity).toList();
                    long totalRecords = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) totalRecords / size);
                    return createPaginatedResponse(products, totalRecords, page, totalPages);
                });
    }

    @GetMapping(value = "/top-selling", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> getTopSelling(
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        int pageIndex = page > 0 ? page - 1 : 0;
        return productService.getTopSelling(limit)
                .collectList()
                .map(products -> {
                    int totalRecords = products.size();
                    int totalPages = (int) Math.ceil((double) totalRecords / size);
                    int fromIndex = Math.min(pageIndex * size, totalRecords);
                    int toIndex = Math.min(fromIndex + size, totalRecords);
                    List<ProductDto> dtos = products.subList(fromIndex, toIndex).stream()
                            .map(ProductDto::fromEntity).toList();
                    return createPaginatedResponse(dtos, totalRecords, page, totalPages);
                });
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ProductDto> getProductById(@PathVariable(name = "id") Long id) {
        return productService.getProductById(id)
                .map(ProductDto::fromEntity)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with id: " + id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        return productService.createProduct(ProductDto.toEntity(productDto))
                .map(ProductDto::fromEntity);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ProductDto> updateProductPartial(@PathVariable(name = "id") Long id,
            @RequestBody Map<String, Object> updates) {
        return productService.updateProductPartial(id, updates)
                .map(ProductDto::fromEntity);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProduct(@PathVariable(name = "id") Long id) {
        return productService.deleteProduct(id);
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public Flux<ProductDto> bulkInsert(@RequestBody Flux<ProductDto> products) {
        return productService.bulkInsert(products.map(ProductDto::toEntity))
                .map(ProductDto::fromEntity);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductDto> streamProducts() {
        return productService.getAllProducts()
                .map(ProductDto::fromEntity);
    }

    @GetMapping("/search")
    public Mono<Map<String, Object>> searchProducts(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "available", required = false) Boolean available,
            @RequestParam(name = "minSales", required = false) Integer minSales,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        return productService.searchProducts(name, category, minPrice, maxPrice, available, minSales, page, size)
                .map(ProductDto::fromEntity)
                .collectList()
                .zipWith(productService.countSearchProducts(name, category, minPrice, maxPrice, available, minSales))
                .map(tuple -> {
                    List<ProductDto> products = tuple.getT1();
                    long totalRecords = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) totalRecords / size);
                    return createPaginatedResponse(products, totalRecords, page, totalPages);
                });
    }

    /**
     * Método utilitario para crear respuestas paginadas.
     */
    private Map<String, Object> createPaginatedResponse(List<ProductDto> products, long totalRecords,
            int currentPage, int totalPages) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", products);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("total_records", totalRecords);
        pagination.put("current_page", currentPage);
        pagination.put("total_pages", totalPages);
        pagination.put("next_page", currentPage < totalPages ? currentPage + 1 : null);
        pagination.put("prev_page", currentPage > 1 ? currentPage - 1 : null);
        response.put("pagination", pagination);

        return response;
    }
}