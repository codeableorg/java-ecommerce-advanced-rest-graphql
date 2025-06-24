package com.ecommerce;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.ecommerce.product.model.Product;
import com.ecommerce.product.service.ProductService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
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
        // Note: available and sales fields removed
        when(productService.getProducts(any())).thenReturn(Flux.just(p));
        when(productService.countProducts()).thenReturn(Mono.just(1L));
        webTestClient.get()
                .uri("/api/v1/products?page=1&size=5")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data[0].id").isEqualTo(1)
                .jsonPath("$.data[0].name").isEqualTo("Test")
                .jsonPath("$.pagination.totalRecords").isEqualTo(1)
                .jsonPath("$.pagination.currentPage").isEqualTo(1);
    }
}
