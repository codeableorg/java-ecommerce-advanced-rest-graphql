package com.ecommerce.order.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

/**
 * Entidad de orden para el sistema de ecommerce.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
public class Order {
    @Id
    private Long id;
    private Long userId;
    private OrderStatus status;
    private Double totalAmount;
    private LocalDateTime orderDate;
}
