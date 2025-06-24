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
    private String keycloakId;
    private String email;
    private String firstName;
    private String lastName;
    private String address;

    public static UserDto fromEntity(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setKeycloakId(user.getKeycloakId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAddress(user.getAddress());
        return dto;
    }

    public static User toEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setKeycloakId(dto.getKeycloakId());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setAddress(dto.getAddress());
        return user;
    }
}
