package se.kth.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.kth.authservice.domain.AuthUser;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

    boolean existsByUsername(String username);
    Optional<AuthUser> findByUsername(String username);
    boolean existsByRole(AuthUser.Role role);
}