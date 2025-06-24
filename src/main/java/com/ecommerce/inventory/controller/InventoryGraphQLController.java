package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.model.Inventory;
import com.ecommerce.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador GraphQL para inventario.
 */
@Controller
@RequiredArgsConstructor
public class InventoryGraphQLController {
  private final InventoryService inventoryService;

  @QueryMapping
  public Mono<Inventory> inventoryByProductId(@Argument Long productId) {
    return inventoryService.getInventoryByProductId(productId);
  }

  @QueryMapping
  public Flux<Inventory> inventory() {
    return inventoryService.getAllInventory();
  }
}
