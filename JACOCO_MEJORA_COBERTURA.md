# Guía rápida: Mejora tu cobertura JaCoCo en 30 minutos

## 1. Ejecuta los tests y abre el reporte
- Ejecuta:
  ```sh
  mvn clean test jacoco:report
  ```
- Abre `target/site/jacoco/index.html` en tu navegador.

## 2. Busca métodos en rojo o sin cubrir
- Enfócate en los métodos de `ProductService`, `ProductController` y `ProductRepository`.

## 3. Ejemplos fáciles para este proyecto

### ProductServiceTest.java
```java
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
```

### ProductControllerTest.java
```java
@Test
void testStreamProducts() {
    Product p = new Product();
    p.setId(300L);
    when(productService.getAllProducts()).thenReturn(Flux.just(p));
    webTestClient.get()
        .uri("/api/v1/products/stream")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Object.class)
        .hasSize(1);
}

@Test
void testSearchProducts() {
    Product p = new Product();
    p.setId(400L);
    when(productService.searchProducts(any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
        .thenReturn(Flux.just(p));
    when(productService.countSearchProducts(any(), any(), any(), any(), any(), any()))
        .thenReturn(Mono.just(1L));
    webTestClient.get()
        .uri("/api/v1/products/search?name=prod")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.data[0].id").isEqualTo(400)
        .jsonPath("$.pagination.total_records").isEqualTo(1);
}
```

### ProductRepositoryTest.java
```java
@Test
void findByNameContainingIgnoreCase_devuelveProductos() {
    databaseClient.sql("INSERT INTO products (id,name,description,category,price,available,sales) VALUES (500,'abc','desc','cat',10.0,true,1)").fetch().rowsUpdated().block();
    StepVerifier.create(productRepository.findByNameContainingIgnoreCase("a", org.springframework.data.domain.Pageable.unpaged()))
        .expectNextMatches(p -> p.getName().contains("a") || p.getName().contains("A"))
        .thenCancel()
        .verify();
}
```

## 4. Vuelve a ejecutar los tests y revisa el avance
- Ejecuta de nuevo:
  ```sh
  mvn test jacoco:report
  ```
- Abre el reporte y verifica que el porcentaje de cobertura haya subido.

---

**¡Con estos ejemplos verás la diferencia en el reporte de JaCoCo!**
