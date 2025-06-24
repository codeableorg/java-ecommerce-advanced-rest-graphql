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

    @GetMapping("/keycloak/{keycloakId}")
    public Mono<UserDto> getUserByKeycloakId(@PathVariable String keycloakId) {
        return userService.getUserByKeycloakId(keycloakId)
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
        userDto.setId(id);
        return userService.updateUser(UserDto.toEntity(userDto))
                .map(UserDto::fromEntity);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
