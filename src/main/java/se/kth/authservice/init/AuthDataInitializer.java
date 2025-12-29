package se.kth.authservice.init;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import se.kth.authservice.domain.AuthUser;
import se.kth.authservice.repository.AuthUserRepository;

@Configuration
public class AuthDataInitializer {

    @Bean
    ApplicationRunner seedAdmin(
            AuthUserRepository userRepo,
            @Value("${auth.seed.enabled:false}") boolean seedEnabled,
            @Value("${auth.seed.admin.username:admin}") String adminUsername,
            @Value("${auth.seed.admin.password:admin}") String adminPassword
    ) {
        return args -> {
            if (!seedEnabled) return;

            if (!userRepo.existsByRole(AuthUser.Role.ADMIN)) {
                userRepo.save(AuthUser.builder()
                        .username(adminUsername)
                        .password(adminPassword)
                        .role(AuthUser.Role.ADMIN)
                        .active(true)
                        .build());
            }
        };
    }
}