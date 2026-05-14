package com.workforcehub.repository;

import com.workforcehub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByVerificationToken(String token);

    Optional<User> findByResetPasswordToken(String token);

    Optional<User> findByRefreshToken(String refreshToken);

    @Query("SELECT u FROM User u WHERE u.deleted = false")
    java.util.List<User> findAllActive();
}
