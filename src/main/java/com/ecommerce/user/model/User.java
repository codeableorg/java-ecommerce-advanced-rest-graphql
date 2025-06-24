package com.ecommerce.user.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

/**
 * Entidad de usuario para el sistema de ecommerce.
 * Simple user entity for basic authentication and user management.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User {
  @Id
  private Long id;

  @Column("username")
  private String username; // Maps to username column (NOT NULL)

  @Column("email")
  private String email; // Maps to email column (NOT NULL)

  @Column("full_name")
  private String fullName; // Maps to full_name column
}
