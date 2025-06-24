package com.ecommerce.user.repository;

import com.ecommerce.user.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo para usuarios.
 */
@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
  Mono<User> findByEmail(String email);

  Mono<User> findByUsername(String username);
}
