package com.workforcehub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * User entity for authentication and authorization.
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_username", columnList = "username")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_enabled", nullable = false)
    @Builder.Default
    private boolean enabled = false;

    @Column(name = "is_account_non_locked", nullable = false)
    @Builder.Default
    private boolean accountNonLocked = true;

    @Column(name = "failed_login_attempts")
    @Builder.Default
    private int failedLoginAttempts = 0;

    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Employee employee;

    /**
     * Add a role to this user.
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Remove a role from this user.
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    /**
     * Increment failed login attempts.
     */
    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.accountNonLocked = false;
        }
    }

    /**
     * Reset failed login attempts.
     */
    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        this.accountNonLocked = true;
    }
}
