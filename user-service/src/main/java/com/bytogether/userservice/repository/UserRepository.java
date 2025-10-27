package com.bytogether.userservice.repository;

import com.bytogether.userservice.model.InitialProvider;
import com.bytogether.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndDeletedAtIsNull(Long id);
    Optional<User> findByEmailAndProviderAndDeletedAtIsNull(String email, InitialProvider provider);
    Optional<User> findByIdAndProviderAndDeletedAtIsNull(Long id, InitialProvider provider);
    boolean existsByEmailAndDeletedAtIsNull(String email);
    boolean existsByNicknameAndDeletedAtIsNull(String nickname) ;
    Optional<User> findByProviderIdAndDeletedAtIsNull(String providerId);
}
