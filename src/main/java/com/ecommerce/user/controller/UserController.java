package com.ecommerce.user.controller;

import com.ecommerce.user.dto.UserDto;
import com.ecommerce.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador REST para usuarios.
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping
  public Flux<UserDto> getAllUsers() {
    return userService.getAllUsers()
        .map(UserDto::fromEntity);
  }

  @GetMapping("/{id}")
  public Mono<UserDto> getUserById(@PathVariable Long id) {
    return userService.getUserById(id)
        .map(UserDto::fromEntity)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
  }

  @GetMapping("/username/{username}")
  public Mono<UserDto> getUserByUsername(@PathVariable String username) {
    return userService.getUserByUsername(username)
        .map(UserDto::fromEntity)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
  }

  @GetMapping("/email/{email}")
  public Mono<UserDto> getUserByEmail(@PathVariable String email) {
    return userService.getUserByEmail(email)
        .map(UserDto::fromEntity)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
  }

  @PostMapping
  public Mono<UserDto> createUser(@RequestBody UserDto userDto) {
    return userService.createUser(UserDto.toEntity(userDto))
        .map(UserDto::fromEntity);
  }

  @PutMapping("/{id}")
  public Mono<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
    // PUT requires full resource replacement - validate required fields
    if (userDto.getUsername() == null || userDto.getEmail() == null) {
      return Mono.error(
          new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and email are required for PUT operation"));
    }
    userDto.setId(id);
    return userService.updateUser(UserDto.toEntity(userDto))
        .map(UserDto::fromEntity);
  }

  @PatchMapping("/{id}")
  public Mono<UserDto> patchUser(@PathVariable Long id, @RequestBody UserDto userDto) {
    // PATCH allows partial updates - merge with existing user
    return userService.getUserById(id)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
        .flatMap(existingUser -> {
          UserDto existingDto = UserDto.fromEntity(existingUser);
          // Only update non-null fields from the request
          if (userDto.getUsername() != null) {
            existingDto.setUsername(userDto.getUsername());
          }
          if (userDto.getEmail() != null) {
            existingDto.setEmail(userDto.getEmail());
          }
          if (userDto.getFullName() != null) {
            existingDto.setFullName(userDto.getFullName());
          }
          return userService.updateUser(UserDto.toEntity(existingDto))
              .map(UserDto::fromEntity);
        });
  }

  @DeleteMapping("/{id}")
  public Mono<Void> deleteUser(@PathVariable Long id) {
    return userService.deleteUser(id);
  }
}
