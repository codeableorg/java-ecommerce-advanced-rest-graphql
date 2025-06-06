package com.ecommerce;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
}
