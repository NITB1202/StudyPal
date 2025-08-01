package com.study.studypal.entities;

import com.study.studypal.converters.AuthProviderListConverter;
import com.study.studypal.enums.AccountRole;
import com.study.studypal.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Convert(converter = AuthProviderListConverter.class)
    @Column(name = "providers", nullable = false)
    private List<AuthProvider> providers;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "hashed_password")
    private String hashedPassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private AccountRole role;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
}
