package com.ecommerce;

import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductById_returnsProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        when(productRepository.findById(1L)).thenReturn(Mono.just(product));

        Mono<Product> result = productService.getProductById(1L);

        StepVerifier.create(result)
                .expectNextMatches(p -> p.getId() == 1L && p.getName().equals("Test Product"))
                .verifyComplete();

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductsByCategory_returnsProducts() {
        Product product = new Product();
        product.setId(2L);
        product.setCategory("cat1");
        when(productRepository.findByCategory(eq("cat1"), any())).thenReturn(reactor.core.publisher.Flux.just(product));

        StepVerifier.create(productService.getProductsByCategory("cat1"))
                .expectNextMatches(p -> p.getCategory().equals("cat1"))
                .verifyComplete();
    }

    @Test
    void getTopSelling_returnsProducts() {
        Product product = new Product();
        product.setId(3L);
        product.setSales(100);
        when(productRepository.findTopSelling(5)).thenReturn(reactor.core.publisher.Flux.just(product));

        StepVerifier.create(productService.getTopSelling(5))
                .expectNextMatches(p -> p.getSales() == 100)
                .verifyComplete();
    }

    @Test
    void countProducts_returnsCount() {
        when(productRepository.count()).thenReturn(Mono.just(42L));
        StepVerifier.create(productService.countProducts())
                .expectNext(42L)
                .verifyComplete();
    }

    @Test
    void createProduct_savesProduct() {
        Product product = new Product();
        product.setId(4L);
        when(productRepository.save(product)).thenReturn(Mono.just(product));
        StepVerifier.create(productService.createProduct(product))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void updateProductPartial_updatesFields() {
        Product product = new Product();
        product.setId(5L);
        product.setName("Old");
        when(productRepository.findById(5L)).thenReturn(Mono.just(product));
        when(productRepository.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        java.util.Map<String, Object> updates = java.util.Map.of("name", "New");
        StepVerifier.create(productService.updateProductPartial(5L, updates))
                .expectNextMatches(p -> p.getName().equals("New"))
                .verifyComplete();
    }

    @Test
    void deleteProduct_deletesById() {
        when(productRepository.deleteById(6L)).thenReturn(Mono.empty());
        StepVerifier.create(productService.deleteProduct(6L))
                .verifyComplete();
    }

    @Test
    void getAllProducts_devuelveProductos() {
        Product p = new Product();
        p.setId(100L);
        when(productRepository.findAll()).thenReturn(Flux.just(p));
        StepVerifier.create(productService.getAllProducts())
                .expectNext(p)
                .verifyComplete();
    }

    @Test
    void bulkInsert_insertaProductos() {
        Product p = new Product();
        p.setId(200L);
        when(productRepository.saveAll(any(Flux.class))).thenReturn(Flux.just(p));
        StepVerifier.create(productService.bulkInsert(Flux.just(p)))
                .expectNext(p)
                .verifyComplete();
    }
}
