package com.bytogether.userservice.repository;

import com.bytogether.userservice.model.InitialProvider;
import com.bytogether.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> findByEmailAndProvider(String email, InitialProvider provider);
    Optional<User> findByIdAndProvider(Long id, InitialProvider provider);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname) ;
    Optional<User> findByProviderId(String providerId);
}
