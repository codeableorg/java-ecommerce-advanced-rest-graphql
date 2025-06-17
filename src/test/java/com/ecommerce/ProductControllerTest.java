package com.ecommerce;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;

@WebFluxTest(com.ecommerce.controller.ProductController.class)
@ActiveProfiles("test")
public class ProductControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProductService productService;

    @Test
    void testGetProducts() {
        // Producto de ejemplo
        Product p = new Product();
        p.setId(1L);
        p.setName("Test");
        p.setDescription("desc");
        p.setPrice(10.0);
        p.setCategory("cat");
        p.setAvailable(true);
        p.setSales(5);
        when(productService.getProducts(any())).thenReturn(Flux.just(p));
        when(productService.countProducts()).thenReturn(Mono.just(1L));
        webTestClient.get()
                .uri("/api/v1/products?page=1&size=5")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data[0].id").isEqualTo(1)
                .jsonPath("$.data[0].name").isEqualTo("Test")
                .jsonPath("$.pagination.total_records").isEqualTo(1)
                .jsonPath("$.pagination.current_page").isEqualTo(1);
    }

    @Test
    void testGetTopSelling() {
        Product p = new Product();
        p.setId(2L);
        p.setName("Top");
        p.setSales(100);
        when(productService.getTopSelling(10)).thenReturn(Flux.just(p));
        webTestClient.get()
                .uri("/api/v1/products/top-selling?limit=10&page=1&size=20")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data[0].id").isEqualTo(2)
                .jsonPath("$.data[0].name").isEqualTo("Top")
                .jsonPath("$.pagination.total_records").isEqualTo(1)
                .jsonPath("$.pagination.current_page").isEqualTo(1);
    }

    @Test
    void testGetProductById() {
        Product p = new Product();
        p.setId(10L);
        p.setName("ById");
        when(productService.getProductById(10L)).thenReturn(Mono.just(p));
        webTestClient.get()
                .uri("/api/v1/products/10")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(10)
                .jsonPath("$.name").isEqualTo("ById");
    }

    @Test
    void testCreateProduct() {
        ProductDto dto = new ProductDto();
        dto.setName("Nuevo");
        Product saved = new Product();
        saved.setId(20L);
        saved.setName("Nuevo");
        when(productService.createProduct(any())).thenReturn(Mono.just(saved));
        webTestClient.post()
                .uri("/api/v1/products")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(20)
                .jsonPath("$.name").isEqualTo("Nuevo");
    }

    @Test
    void testUpdateProductPartial() {
        Product updated = new Product();
        updated.setId(30L);
        updated.setName("Actualizado");
        when(productService.updateProductPartial(eq(30L), any())).thenReturn(Mono.just(updated));
        webTestClient.patch()
                .uri("/api/v1/products/30")
                .bodyValue(java.util.Map.of("name", "Actualizado"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(30)
                .jsonPath("$.name").isEqualTo("Actualizado");
    }

    @Test
    void testDeleteProduct() {
        when(productService.deleteProduct(40L)).thenReturn(Mono.empty());
        webTestClient.delete()
                .uri("/api/v1/products/40")
                .exchange()
                .expectStatus().isOk();
    }
}
