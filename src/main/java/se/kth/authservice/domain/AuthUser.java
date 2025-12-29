package se.kth.authservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "auth_user",
        uniqueConstraints = {@UniqueConstraint(name = "uk_authuser_username", columnNames = "username")}
)
public class AuthUser {

    public enum Role { ADMIN, PATIENT, PRACTITIONER }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
}