package se.kth.authservice.service;

import org.springframework.stereotype.Service;
import se.kth.authservice.domain.AuthUser;
import se.kth.authservice.repository.AuthUserRepository;

import java.util.List;

import static se.kth.authservice.web.dto.UserDto.*;

@Service
public class UserService {

    private final AuthUserRepository userRepo;

    public UserService(AuthUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<UserResponse> findAll() {
        return userRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private UserResponse toResponse(AuthUser u) {
        // Vi kan inte läsa organisation eller namn från journal-service här,
        // så de sätts till null. Frontend kan ändå visa username + role.
        return new UserResponse(
                u.getId(),
                u.getUsername(),
                u.getRole(),
                u.isActive(),
                null,
                null,
                null,
                null
        );
    }
}