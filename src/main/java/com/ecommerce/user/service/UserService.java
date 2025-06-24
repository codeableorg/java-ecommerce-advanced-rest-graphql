package com.ecommerce.user.service;

import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Servicio para la lógica de negocio de usuarios.
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public Flux<User> getAllUsers() {
    return userRepository.findAll();
  }

  public Mono<User> getUserById(Long id) {
    return userRepository.findById(id);
  }

  public Mono<User> getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public Mono<User> getUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public Mono<User> createUser(User user) {
    return userRepository.save(user);
  }

  public Mono<User> updateUser(User user) {
    return userRepository.save(user);
  }

  public Mono<Void> deleteUser(Long id) {
    return userRepository.deleteById(id);
  }
}
