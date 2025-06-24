package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductDto;
import com.ecommerce.product.service.ProductService;
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

  // Note: Top selling endpoint will be moved to Order service later
  // @GetMapping(value = "/top-selling", produces =
  // MediaType.APPLICATION_JSON_VALUE)
  // public Mono<Map<String, Object>> getTopSelling(...) { ... }

  @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Map<String, Object>> searchProducts(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) Double minPrice,
      @RequestParam(required = false) Double maxPrice,
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = "20") int size) {

    return productService.searchProducts(name, category, minPrice, maxPrice, page, size)
        .collectList()
        .zipWith(productService.countSearchProducts(name, category, minPrice, maxPrice))
        .map(tuple -> {
          List<ProductDto> products = tuple.getT1().stream().map(ProductDto::fromEntity).toList();
          long totalRecords = tuple.getT2();
          int totalPages = (int) Math.ceil((double) totalRecords / size);
          return createPaginatedResponse(products, totalRecords, page, totalPages);
        });
  }

  @GetMapping("/{id}")
  public Mono<ProductDto> getProductById(@PathVariable Long id) {
    return productService.getProductById(id)
        .map(ProductDto::fromEntity)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")));
  }

  @PostMapping
  public Mono<ProductDto> createProduct(@RequestBody ProductDto productDto) {
    return productService.createProduct(ProductDto.toEntity(productDto))
        .map(ProductDto::fromEntity);
  }

  @PatchMapping("/{id}")
  public Mono<ProductDto> updateProductPartial(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
    return productService.updateProductPartial(id, updates)
        .map(ProductDto::fromEntity)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")));
  }

  @DeleteMapping("/{id}")
  public Mono<Void> deleteProduct(@PathVariable Long id) {
    return productService.deleteProduct(id);
  }

  @PostMapping("/bulk")
  public Flux<ProductDto> bulkCreateProducts(@RequestBody List<ProductDto> productDtos) {
    Flux<com.ecommerce.product.model.Product> products = Flux.fromIterable(productDtos)
        .map(ProductDto::toEntity);
    return productService.bulkInsert(products)
        .map(ProductDto::fromEntity);
  }

  private Map<String, Object> createPaginatedResponse(List<ProductDto> data, long totalRecords, int currentPage,
      int totalPages) {
    Map<String, Object> response = new HashMap<>();
    response.put("data", data);
    response.put("pagination", Map.of(
        "currentPage", currentPage,
        "totalPages", totalPages,
        "totalRecords", totalRecords,
        "pageSize", data.size()));
    return response;
  }
}
