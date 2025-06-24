package com.ecommerce.user.controller;

import com.ecommerce.user.model.User;
import com.ecommerce.user.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador GraphQL para usuarios.
 */
@Controller
@RequiredArgsConstructor
public class UserGraphQLController {
  private final UserService userService;

  @QueryMapping
  public Mono<User> userById(@Argument Long id) {
    return userService.getUserById(id);
  }

  @QueryMapping
  public Flux<User> users() {
    return userService.getAllUsers();
  }

  @MutationMapping
  public Mono<User> createUser(@Argument("input") UserInput input) {
    User user = new User();
    user.setUsername(input.getUsername());
    user.setEmail(input.getEmail());
    user.setFullName(input.getFullName());
    return userService.createUser(user);
  }

  @MutationMapping
  public Mono<User> updateUser(@Argument Long id, @Argument("input") UserInput input) {
    User user = new User();
    user.setId(id);
    user.setUsername(input.getUsername());
    user.setEmail(input.getEmail());
    user.setFullName(input.getFullName());
    return userService.updateUser(user);
  }

  @MutationMapping
  public Mono<Boolean> deleteUser(@Argument Long id) {
    return userService.deleteUser(id)
        .then(Mono.just(true))
        .onErrorReturn(false);
  }

  @Data
  public static class UserInput {
    private String username;
    private String email;
    private String fullName;
  }
}
