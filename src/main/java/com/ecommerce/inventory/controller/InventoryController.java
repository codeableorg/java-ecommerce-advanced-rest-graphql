package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.dto.InventoryDto;
import com.ecommerce.inventory.model.Inventory;
import com.ecommerce.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST Controller para inventario.
 */
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

  private final InventoryService inventoryService;

  @GetMapping
  public Flux<InventoryDto> getAllInventory() {
    return inventoryService.getAllInventory()
        .map(this::convertToDto);
  }

  @GetMapping("/{id}")
  public Mono<InventoryDto> getInventoryById(@PathVariable Long id) {
    return inventoryService.getInventoryById(id)
        .map(this::convertToDto);
  }

  @GetMapping("/product/{productId}")
  public Mono<InventoryDto> getInventoryByProductId(@PathVariable Long productId) {
    return inventoryService.getInventoryByProductId(productId)
        .map(this::convertToDto);
  }

  @GetMapping("/product/{productId}/availability")
  public Mono<Boolean> checkProductAvailability(@PathVariable Long productId,
      @RequestParam Integer quantity) {
    return inventoryService.isProductAvailable(productId, quantity);
  }

  @PutMapping("/product/{productId}/stock")
  @ResponseStatus(HttpStatus.OK)
  public Mono<InventoryDto> updateStock(@PathVariable Long productId,
      @RequestParam Integer quantity) {
    return inventoryService.updateStock(productId, quantity)
        .map(this::convertToDto);
  }

  @PutMapping("/product/{productId}/reserve")
  @ResponseStatus(HttpStatus.OK)
  public Mono<InventoryDto> reserveStock(@PathVariable Long productId,
      @RequestParam Integer quantity) {
    return inventoryService.reserveStock(productId, quantity)
        .map(this::convertToDto);
  }

  private InventoryDto convertToDto(Inventory inventory) {
    InventoryDto dto = new InventoryDto();
    dto.setId(inventory.getId());
    dto.setProductId(inventory.getProductId());
    dto.setStockQuantity(inventory.getStockQuantity());
    return dto;
  }
}
