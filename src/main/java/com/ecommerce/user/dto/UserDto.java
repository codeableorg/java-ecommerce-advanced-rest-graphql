package com.ecommerce.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.ecommerce.user.model.User;

/**
 * DTO para transferir datos de usuario.
 */
@Data
@NoArgsConstructor
public class UserDto {
  private Long id;
  private String username;
  private String email;
  private String fullName;

  public static UserDto fromEntity(User user) {
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setFullName(user.getFullName());
    return dto;
  }

  public static User toEntity(UserDto dto) {
    User user = new User();
    user.setId(dto.getId());
    user.setUsername(dto.getUsername());
    user.setEmail(dto.getEmail());
    user.setFullName(dto.getFullName());
    return user;
  }
}
