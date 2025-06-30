# Configuración API Gateway con Circuit Breaker y Retry

Este documento proporciona una guía paso a paso para configurar un API Gateway con Spring Cloud Gateway, implementando patrones de Circuit Breaker y Retry para alta disponibilidad y resiliencia.

## Tabla de Contenidos

1. [Dependencias Maven](#dependencias-maven)
2. [Configuración Application Properties](#configuración-application-properties)
3. [Configuración de Circuit Breaker](#configuración-de-circuit-breaker)
4. [Configuración de Retry](#configuración-de-retry)
5. [Configuración del Gateway](#configuración-del-gateway)
6. [Filtros Personalizados](#filtros-personalizados)
7. [Manejo de Excepciones](#manejo-de-excepciones)
8. [Métricas y Monitoreo](#métricas-y-monitoreo)
9. [Testing](#testing)
10. [Mejores Prácticas](#mejores-prácticas)

## 1. Dependencias Maven

Primero, actualiza tu `pom.xml` con las dependencias necesarias:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <groupId>com.ecommerce</groupId>
    <artifactId>ecommerce-api-gateway</artifactId>
    <version>1.0.0</version>
    <name>ecommerce-api-gateway</name>
    <description>API Gateway for Ecommerce Microservices</description>
    
    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
        <resilience4j.version>2.1.0</resilience4j.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- Spring Cloud Gateway -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
        
        <!-- Circuit Breaker - Resilience4j -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
        </dependency>
        
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-spring-boot3</artifactId>
            <version>${resilience4j.version}</version>
        </dependency>
        
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-reactor</artifactId>
            <version>${resilience4j.version}</version>
        </dependency>
        
        <!-- Service Discovery (opcional) -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-loadbalancer</artifactId>
        </dependency>
        
        <!-- Métricas y Monitoring -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>io.projectreactor</groupId>
            <artifactId>reactor-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

## 2. Configuración Application Properties

Crea el archivo `application.yml` con la configuración básica:

```yaml
server:
  port: 8080

spring:
  application:
    name: ecommerce-api-gateway
  
  cloud:
    gateway:
      routes:
        # Ruta para el servicio de productos
        - id: product-service
          uri: http://localhost:8081
          predicates:
            - Path=/api/products/**
          filters:
            - name: CircuitBreaker
              args:
                name: product-service-cb
                fallbackUri: forward:/fallback/products
            - name: Retry
              args:
                retries: 3
                statuses: BAD_GATEWAY,GATEWAY_TIMEOUT
                methods: GET,POST,PUT,DELETE
                backoff:
                  firstBackoff: 50ms
                  maxBackoff: 500ms
                  factor: 2
                  basedOnPreviousValue: false
        
        # Ruta para el servicio de usuarios
        - id: user-service
          uri: http://localhost:8082
          predicates:
            - Path=/api/users/**
          filters:
            - name: CircuitBreaker
              args:
                name: user-service-cb
                fallbackUri: forward:/fallback/users
            - name: Retry
              args:
                retries: 3
                statuses: BAD_GATEWAY,GATEWAY_TIMEOUT
                methods: GET,POST,PUT,DELETE
        
        # Ruta para el servicio de órdenes
        - id: order-service
          uri: http://localhost:8083
          predicates:
            - Path=/api/orders/**
          filters:
            - name: CircuitBreaker
              args:
                name: order-service-cb
                fallbackUri: forward:/fallback/orders
            - name: Retry
              args:
                retries: 2
                statuses: BAD_GATEWAY,GATEWAY_TIMEOUT
                methods: GET,POST,PUT,DELETE
        
        # Ruta para el servicio de inventario
        - id: inventory-service
          uri: http://localhost:8084
          predicates:
            - Path=/api/inventory/**
          filters:
            - name: CircuitBreaker
              args:
                name: inventory-service-cb
                fallbackUri: forward:/fallback/inventory
            - name: Retry
              args:
                retries: 3
                statuses: BAD_GATEWAY,GATEWAY_TIMEOUT
                methods: GET,POST,PUT,DELETE

# Configuración de Resilience4j
resilience4j:
  circuitbreaker:
    configs:
      default:
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 30s
        slidingWindowType: COUNT_BASED
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
        recordExceptions:
          - java.net.ConnectException
          - java.util.concurrent.TimeoutException
          - org.springframework.web.reactive.function.client.WebClientRequestException
        ignoreExceptions:
          - java.lang.IllegalArgumentException
    instances:
      product-service-cb:
        baseConfig: default
        slidingWindowSize: 20
        minimumNumberOfCalls: 10
        failureRateThreshold: 60
      user-service-cb:
        baseConfig: default
        slidingWindowSize: 15
        minimumNumberOfCalls: 8
        failureRateThreshold: 55
      order-service-cb:
        baseConfig: default
        slidingWindowSize: 25
        minimumNumberOfCalls: 12
        failureRateThreshold: 40
        waitDurationInOpenState: 60s
      inventory-service-cb:
        baseConfig: default
  
  retry:
    configs:
      default:
        maxAttempts: 3
        waitDuration: 1s
        retryExceptions:
          - java.net.ConnectException
          - java.util.concurrent.TimeoutException
          - org.springframework.web.reactive.function.client.WebClientRequestException
        ignoreExceptions:
          - java.lang.IllegalArgumentException
    instances:
      product-service-retry:
        baseConfig: default
        maxAttempts: 4
        waitDuration: 500ms
      user-service-retry:
        baseConfig: default
        maxAttempts: 3
        waitDuration: 750ms
      order-service-retry:
        baseConfig: default
        maxAttempts: 2
        waitDuration: 1s
      inventory-service-retry:
        baseConfig: default

# Configuración de Actuator para métricas
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,circuitbreakers,circuitbreakerevents
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
    distribution:
      percentiles-histogram:
        http.server.requests: true
      percentiles:
        http.server.requests: 0.5, 0.95, 0.99
        resilience4j.circuitbreaker.calls: 0.5, 0.95, 0.99

# Logging
logging:
  level:
    com.ecommerce: DEBUG
    org.springframework.cloud.gateway: DEBUG
    io.github.resilience4j: DEBUG
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
```

## 3. Configuración de Circuit Breaker

Crea la clase de configuración para Circuit Breaker:

```java
package com.ecommerce.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.net.ConnectException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Configuration
public class CircuitBreakerConfiguration {

    @Bean
    public CircuitBreakerConfigCustomizer circuitBreakerConfigCustomizer() {
        return CircuitBreakerConfigCustomizer.of("default", builder -> builder
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50.0f)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .permittedNumberOfCallsInHalfOpenState(3)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .recordExceptions(
                        ConnectException.class,
                        TimeoutException.class,
                        WebClientRequestException.class
                )
                .ignoreExceptions(IllegalArgumentException.class)
        );
    }

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.ofDefaults();
    }
}
```

## 4. Configuración de Retry

Crea la clase de configuración para Retry:

```java
package com.ecommerce.config;

import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.net.ConnectException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Configuration
public class RetryConfiguration {

    @Bean
    public RetryConfigCustomizer retryConfigCustomizer() {
        return RetryConfigCustomizer.of("default", builder -> builder
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(1000))
                .retryExceptions(
                        ConnectException.class,
                        TimeoutException.class,
                        WebClientRequestException.class
                )
                .ignoreExceptions(IllegalArgumentException.class)
        );
    }

    @Bean
    public RetryRegistry retryRegistry() {
        return RetryRegistry.ofDefaults();
    }
}
```

## 5. Configuración del Gateway

Crea la clase principal de configuración del Gateway:

```java
package com.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.time.Duration;

@Configuration
public class GatewayConfig {

    @Value("${services.product.url:http://localhost:8081}")
    private String productServiceUrl;

    @Value("${services.user.url:http://localhost:8082}")
    private String userServiceUrl;

    @Value("${services.order.url:http://localhost:8083}")
    private String orderServiceUrl;

    @Value("${services.inventory.url:http://localhost:8084}")
    private String inventoryServiceUrl;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Product Service Routes
                .route("product-service", r -> r
                        .path("/api/products/**")
                        .and()
                        .method(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("product-service-cb")
                                        .setFallbackUri("forward:/fallback/products")
                                        .setRouteId("product-service"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setStatuses("BAD_GATEWAY", "GATEWAY_TIMEOUT")
                                        .setMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                                        .setBackoff(Duration.ofMillis(50), Duration.ofMillis(500), 2, false))
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(hostAddrKeyResolver()))
                        )
                        .uri(productServiceUrl))

                // User Service Routes
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("user-service-cb")
                                        .setFallbackUri("forward:/fallback/users"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setStatuses("BAD_GATEWAY", "GATEWAY_TIMEOUT"))
                        )
                        .uri(userServiceUrl))

                // Order Service Routes
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("order-service-cb")
                                        .setFallbackUri("forward:/fallback/orders"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setStatuses("BAD_GATEWAY", "GATEWAY_TIMEOUT"))
                        )
                        .uri(orderServiceUrl))

                // Inventory Service Routes
                .route("inventory-service", r -> r
                        .path("/api/inventory/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("inventory-service-cb")
                                        .setFallbackUri("forward:/fallback/inventory"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setStatuses("BAD_GATEWAY", "GATEWAY_TIMEOUT"))
                        )
                        .uri(inventoryServiceUrl))
                .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10, 20, 1);
    }

    @Bean
    public KeyResolver hostAddrKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress()
        );
    }
}
```

## 6. Filtros Personalizados

### Global Filter para Logging y Métricas

```java
package com.ecommerce.filter;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GlobalLoggingFilter.class);
    private final MeterRegistry meterRegistry;

    public GlobalLoggingFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestId = exchange.getRequest().getId();
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod().toString();
        
        log.info("Request started - ID: {}, Method: {}, Path: {}", requestId, method, path);
        
        Timer.Sample sample = Timer.start(meterRegistry);
        
        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    sample.stop(Timer.builder("gateway.request.duration")
                            .tag("method", method)
                            .tag("path", path)
                            .tag("status", String.valueOf(exchange.getResponse().getStatusCode().value()))
                            .register(meterRegistry));
                    
                    log.info("Request completed - ID: {}, Status: {}", 
                            requestId, exchange.getResponse().getStatusCode());
                })
                .doOnError(throwable -> {
                    sample.stop(Timer.builder("gateway.request.duration")
                            .tag("method", method)
                            .tag("path", path)
                            .tag("status", "error")
                            .register(meterRegistry));
                    
                    log.error("Request failed - ID: {}, Error: {}", requestId, throwable.getMessage());
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
```

### Circuit Breaker Event Listener

```java
package com.ecommerce.filter;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CircuitBreakerEventListener implements RegistryEventConsumer<CircuitBreaker> {

    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerEventListener.class);
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerEventListener(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @PostConstruct
    public void init() {
        circuitBreakerRegistry.getEventPublisher().onEntryAdded(this);
        circuitBreakerRegistry.getEventPublisher().onEntryRemoved(this);
        circuitBreakerRegistry.getEventPublisher().onEntryReplaced(this);
    }

    @Override
    public void onEntryAddedEvent(EntryAddedEvent<CircuitBreaker> entryAddedEvent) {
        CircuitBreaker circuitBreaker = entryAddedEvent.getAddedEntry();
        circuitBreaker.getEventPublisher()
                .onStateTransition(event -> 
                    log.info("Circuit Breaker {} transitioned from {} to {}",
                            circuitBreaker.getName(),
                            event.getStateTransition().getFromState(),
                            event.getStateTransition().getToState()))
                .onFailureRateExceeded(event ->
                    log.warn("Circuit Breaker {} failure rate exceeded: {}%",
                            circuitBreaker.getName(),
                            event.getFailureRate()))
                .onSlowCallRateExceeded(event ->
                    log.warn("Circuit Breaker {} slow call rate exceeded: {}%",
                            circuitBreaker.getName(),
                            event.getSlowCallRate()))
                .onCallNotPermitted(event ->
                    log.warn("Circuit Breaker {} call not permitted",
                            circuitBreaker.getName()));
    }

    @Override
    public void onEntryRemovedEvent(EntryRemovedEvent<CircuitBreaker> entryRemoveEvent) {
        log.info("Circuit Breaker {} removed", entryRemoveEvent.getRemovedEntry().getName());
    }

    @Override
    public void onEntryReplacedEvent(EntryReplacedEvent<CircuitBreaker> entryReplacedEvent) {
        log.info("Circuit Breaker {} replaced", entryReplacedEvent.getNewEntry().getName());
    }
}
```

## 7. Manejo de Excepciones

### Fallback Controller

```java
package com.ecommerce.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/products")
    public Mono<ResponseEntity<Map<String, Object>>> productsFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "message", "Product service is temporarily unavailable",
                        "timestamp", LocalDateTime.now(),
                        "service", "product-service",
                        "fallback", true
                )));
    }

    @PostMapping("/products")
    @PutMapping("/products")
    @DeleteMapping("/products")
    public Mono<ResponseEntity<Map<String, Object>>> productsWriteFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "message", "Product service write operations are temporarily unavailable",
                        "timestamp", LocalDateTime.now(),
                        "service", "product-service",
                        "fallback", true,
                        "suggestion", "Please try again later"
                )));
    }

    @GetMapping("/users")
    public Mono<ResponseEntity<Map<String, Object>>> usersFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "message", "User service is temporarily unavailable",
                        "timestamp", LocalDateTime.now(),
                        "service", "user-service",
                        "fallback", true
                )));
    }

    @GetMapping("/orders")
    public Mono<ResponseEntity<Map<String, Object>>> ordersFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "message", "Order service is temporarily unavailable",
                        "timestamp", LocalDateTime.now(),
                        "service", "order-service",
                        "fallback", true,
                        "cached_data", getCachedOrderData()
                )));
    }

    @GetMapping("/inventory")
    public Mono<ResponseEntity<Map<String, Object>>> inventoryFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "message", "Inventory service is temporarily unavailable",
                        "timestamp", LocalDateTime.now(),
                        "service", "inventory-service",
                        "fallback", true
                )));
    }

    private Map<String, Object> getCachedOrderData() {
        // Aquí podrías implementar lógica para devolver datos cacheados
        return Map.of(
                "cached", true,
                "note", "Showing last known order status"
        );
    }
}
```

### Global Exception Handler

```java
package com.ecommerce.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        
        HttpStatus status = determineStatus(ex);
        response.setStatusCode(status);
        
        String errorMessage = createErrorMessage(ex, status);
        
        log.error("Gateway error: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        
        DataBuffer buffer = response.bufferFactory().wrap(errorMessage.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    private HttpStatus determineStatus(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            return ((ResponseStatusException) ex).getStatus();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String createErrorMessage(Throwable ex, HttpStatus status) {
        return String.format("""
                {
                    "timestamp": "%s",
                    "status": %d,
                    "error": "%s",
                    "message": "%s",
                    "path": "gateway"
                }
                """,
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred"
        );
    }
}
```

## 8. Métricas y Monitoreo

### Health Indicator Personalizado

```java
package com.ecommerce.health;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.boot.actuator.health.Health;
import org.springframework.boot.actuator.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CircuitBreakerHealthIndicator implements HealthIndicator {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerHealthIndicator(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();
        boolean allHealthy = true;

        for (CircuitBreaker circuitBreaker : circuitBreakerRegistry.getAllCircuitBreakers()) {
            String name = circuitBreaker.getName();
            CircuitBreaker.State state = circuitBreaker.getState();
            
            Map<String, Object> cbDetails = new HashMap<>();
            cbDetails.put("state", state.toString());
            cbDetails.put("failureRate", circuitBreaker.getMetrics().getFailureRate());
            cbDetails.put("callsCount", circuitBreaker.getMetrics().getNumberOfBufferedCalls());
            cbDetails.put("failedCalls", circuitBreaker.getMetrics().getNumberOfFailedCalls());
            cbDetails.put("successfulCalls", circuitBreaker.getMetrics().getNumberOfSuccessfulCalls());
            
            details.put(name, cbDetails);
            
            if (state == CircuitBreaker.State.OPEN) {
                allHealthy = false;
            }
        }

        return allHealthy ? 
                Health.up().withDetails(details).build() : 
                Health.down().withDetails(details).build();
    }
}
```

### Configuración de Métricas Prometheus

```java
package com.ecommerce.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public MeterBinder circuitBreakerMetrics(CircuitBreakerRegistry circuitBreakerRegistry) {
        return registry -> circuitBreakerRegistry.getAllCircuitBreakers()
                .forEach(circuitBreaker -> {
                    circuitBreaker.getEventPublisher()
                            .onStateTransition(event -> 
                                    registry.counter("circuitbreaker.state.transition",
                                            "name", circuitBreaker.getName(),
                                            "from", event.getStateTransition().getFromState().toString(),
                                            "to", event.getStateTransition().getToState().toString())
                                            .increment())
                            .onCallNotPermitted(event ->
                                    registry.counter("circuitbreaker.call.not.permitted",
                                            "name", circuitBreaker.getName())
                                            .increment());
                });
    }

    @Bean
    public MeterBinder retryMetrics(RetryRegistry retryRegistry) {
        return registry -> retryRegistry.getAllRetries()
                .forEach(retry -> 
                        retry.getEventPublisher()
                                .onRetry(event ->
                                        registry.counter("retry.calls",
                                                "name", retry.getName(),
                                                "attempt", String.valueOf(event.getNumberOfRetryAttempts()))
                                                .increment()));
    }
}
```

## 9. Testing

### Test de Integración para Circuit Breaker

```java
package com.ecommerce;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "services.product.url=http://localhost:8089",
        "resilience4j.circuitbreaker.instances.product-service-cb.minimum-number-of-calls=2",
        "resilience4j.circuitbreaker.instances.product-service-cb.failure-rate-threshold=50"
})
class CircuitBreakerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(options().port(8089));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void shouldOpenCircuitBreakerAfterFailures() {
        // Configurar mock para fallar
        stubFor(get(urlPathMatching("/api/products/.*"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("product-service-cb");

        // Hacer llamadas que fallarán
        for (int i = 0; i < 3; i++) {
            webTestClient.get()
                    .uri("/api/products/1")
                    .exchange()
                    .expectStatus().is5xxServerError();
        }

        // Verificar que el circuit breaker está abierto
        assert circuitBreaker.getState() == CircuitBreaker.State.OPEN;

        // La siguiente llamada debería ir al fallback
        webTestClient.get()
                .uri("/api/products/1")
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.fallback").isEqualTo(true);
    }

    @Test
    void shouldRetryOnFailureAndSucceedEventually() {
        // Primera llamada falla, segunda succeeds
        stubFor(get(urlPathMatching("/api/products/.*"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Temporary failure"))
                .willSetStateTo("First Attempt"));

        stubFor(get(urlPathMatching("/api/products/.*"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("First Attempt")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"id\": 1, \"name\": \"Product 1\"}")
                        .withHeader("Content-Type", "application/json")));

        webTestClient.get()
                .uri("/api/products/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);

        // Verificar que se realizaron múltiples intentos
        verify(moreThan(1), getRequestedFor(urlPathMatching("/api/products/.*")));
    }
}
```

### Test de Performance

```java
package com.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayPerformanceTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldHandleConcurrentRequests() throws InterruptedException {
        int numberOfRequests = 100;
        CountDownLatch latch = new CountDownLatch(numberOfRequests);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < numberOfRequests; i++) {
            executor.submit(() -> {
                try {
                    webTestClient.get()
                            .uri("/api/products")
                            .exchange()
                            .expectStatus().is2xxSuccessful()
                            .returnResult(String.class)
                            .getResponseBody()
                            .blockFirst(Duration.ofSeconds(5));
                    
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        System.out.println("Success: " + successCount.get() + ", Errors: " + errorCount.get());
        assert successCount.get() > numberOfRequests * 0.8; // 80% success rate
    }

    @Test
    void shouldMaintainPerformanceUnderLoad() {
        Flux<String> requests = Flux.range(1, 50)
                .flatMap(i -> webTestClient.get()
                        .uri("/api/products")
                        .exchange()
                        .expectStatus().is2xxSuccessful()
                        .returnResult(String.class)
                        .getResponseBody()
                        .single(), 10); // Max concurrency of 10

        StepVerifier.create(requests)
                .expectNextCount(50)
                .verifyComplete();
    }
}
```

## 10. Mejores Prácticas

### 1. Configuración por Ambiente

Crea archivos específicos para cada ambiente:

**application-dev.yml:**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      product-service-cb:
        failureRateThreshold: 70
        waitDurationInOpenState: 10s
      user-service-cb:
        failureRateThreshold: 70
        waitDurationInOpenState: 10s

logging:
  level:
    com.ecommerce: DEBUG
    io.github.resilience4j: DEBUG
```

**application-prod.yml:**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      product-service-cb:
        failureRateThreshold: 50
        waitDurationInOpenState: 60s
      user-service-cb:
        failureRateThreshold: 45
        waitDurationInOpenState: 90s

logging:
  level:
    com.ecommerce: WARN
    io.github.resilience4j: INFO
```

### 2. Monitoreo y Alertas

Configura alertas basadas en métricas:

```yaml
# prometheus.yml (ejemplo)
rule_files:
  - "circuit_breaker_rules.yml"

# circuit_breaker_rules.yml
groups:
  - name: circuit_breaker_alerts
    rules:
      - alert: CircuitBreakerOpen
        expr: circuitbreaker_state{state="open"} == 1
        for: 1m
        labels:
          severity: warning
        annotations:
          summary: "Circuit breaker {{ $labels.name }} is open"
          description: "Circuit breaker for {{ $labels.name }} has been open for more than 1 minute"

      - alert: HighFailureRate
        expr: rate(circuitbreaker_calls_total{outcome="failure"}[5m]) > 0.5
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "High failure rate detected"
          description: "Failure rate is {{ $value }} for circuit breaker {{ $labels.name }}"
```

### 3. Consideraciones de Seguridad

```java
// Ejemplo de filtro de autenticación
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        // Validar token JWT aquí
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
```

### 4. Optimización de Performance

```yaml
# application.yml - Optimizaciones
spring:
  cloud:
    gateway:
      httpclient:
        pool:
          max-connections: 500
          max-idle-time: 30s
        connect-timeout: 3000
        response-timeout: 5s
      
server:
  netty:
    connection-timeout: 3s
    h2c-max-content-length: 0
  compression:
    enabled: true
    mime-types: application/json,application/xml,text/html,text/xml,text/plain
```

### 5. Documentación de API

Crea endpoints de documentación:

```java
@RestController
@RequestMapping("/api-docs")
public class ApiDocumentationController {

    @GetMapping("/health")
    public Mono<Map<String, Object>> getHealthInfo() {
        return Mono.just(Map.of(
                "status", "UP",
                "services", Map.of(
                        "product-service", "http://localhost:8081/actuator/health",
                        "user-service", "http://localhost:8082/actuator/health",
                        "order-service", "http://localhost:8083/actuator/health",
                        "inventory-service", "http://localhost:8084/actuator/health"
                )
        ));
    }

    @GetMapping("/routes")
    public Mono<Map<String, Object>> getRoutes() {
        return Mono.just(Map.of(
                "routes", List.of(
                        Map.of("path", "/api/products/**", "service", "product-service"),
                        Map.of("path", "/api/users/**", "service", "user-service"),
                        Map.of("path", "/api/orders/**", "service", "order-service"),
                        Map.of("path", "/api/inventory/**", "service", "inventory-service")
                )
        ));
    }
}
```

## Comandos para Ejecutar el Proyecto

1. **Compilar el proyecto:**
```bash
mvn clean compile
```

2. **Ejecutar tests:**
```bash
mvn test
```

3. **Ejecutar la aplicación:**
```bash
mvn spring-boot:run
```

4. **Generar reporte de cobertura:**
```bash
mvn clean test jacoco:report
```

5. **Construir imagen Docker:**
```bash
docker build -t ecommerce-gateway .
```

## Endpoints de Monitoreo

- **Health Check:** `GET /actuator/health`
- **Métricas:** `GET /actuator/metrics`
- **Circuit Breakers:** `GET /actuator/circuitbreakers`
- **Prometheus:** `GET /actuator/prometheus`
- **Info:** `GET /actuator/info`

## Conclusión

Esta configuración proporciona:

1. **Alta Disponibilidad** mediante Circuit Breakers
2. **Resiliencia** mediante Retry automático
3. **Monitoreo completo** con métricas y health checks
4. **Fallbacks elegantes** cuando los servicios fallan
5. **Testing comprehensivo** para validar el comportamiento
6. **Configuración flexible** por ambiente
7. **Performance optimizada** para alta carga

El API Gateway ahora puede manejar fallos de servicios downstream de manera elegante, proporcionando una experiencia de usuario consistente incluso cuando algunos servicios no están disponibles.
