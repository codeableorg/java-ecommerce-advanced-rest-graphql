package com.ecommerce;

import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataR2dbcTest
@ActiveProfiles("test")
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private DatabaseClient databaseClient;

    @Test
    void findByCategory_returnsProducts() {
        Product p = new Product();
        p.setName("Test");
        p.setDescription("Test description");
        p.setCategory("cat-test");
        p.setPrice(10.0);
        p.setAvailable(true);
        p.setSales(1);
        // Insertar producto de prueba
        databaseClient.sql(
                "INSERT INTO products (id,name,description, category, price, available, sales) VALUES (1,'Test','Test description', 'cat-test', 10.0, true, 1)")
                .fetch().rowsUpdated().block();
        Flux<Product> result = productRepository.findByCategory("cat-test",
                org.springframework.data.domain.Pageable.unpaged());
        StepVerifier.create(result)
                .expectNextMatches(prod -> prod.getCategory().equals("cat-test"))
                .thenCancel()
                .verify();
    }

    @Test
    void findTopSelling_returnsProducts() {
        databaseClient.sql(
                "INSERT INTO products (id,name,description,category,price,available,sales) VALUES (2,'Top','desc','cat',20.0,true,50)")
                .fetch().rowsUpdated().block();
        StepVerifier.create(productRepository.findTopSelling(1))
                .expectNextMatches(p -> p.getSales() == 50)
                .thenCancel()
                .verify();
    }

    @Test
    void count_returnsCount() {
        databaseClient.sql(
                "INSERT INTO products (id,name,description,category,price,available,sales) VALUES (3,'Count','desc','cat',30.0,true,5)")
                .fetch().rowsUpdated().block();
        StepVerifier.create(productRepository.count())
                .expectNextMatches(count -> count > 0)
                .verifyComplete();
    }
}
