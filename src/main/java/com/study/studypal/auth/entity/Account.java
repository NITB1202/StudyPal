package com.study.studypal.auth.entity;

import com.study.studypal.auth.converter.AuthProviderListConverter;
import com.study.studypal.auth.enums.AccountRole;
import com.study.studypal.auth.enums.AuthProvider;
import com.study.studypal.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

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
