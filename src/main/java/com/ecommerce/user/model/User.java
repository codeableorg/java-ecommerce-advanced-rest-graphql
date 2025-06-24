package com.ecommerce.user.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entidad de usuario para el sistema de ecommerce.
 * Keycloak maneja autenticación, esta entidad solo datos de negocio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User {
    @Id
    private Long id;
    private String keycloakId; // References Keycloak user UUID
    private String email;
    private String firstName;
    private String lastName;
    private String address;
}
