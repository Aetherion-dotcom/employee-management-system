package com.workforcehub.service;

import com.workforcehub.dto.request.LoginRequest;
import com.workforcehub.dto.request.RegisterRequest;
import com.workforcehub.dto.response.AuthResponse;
import com.workforcehub.entity.Employee;
import com.workforcehub.entity.Role;
import com.workforcehub.entity.User;
import com.workforcehub.enums.EmployeeStatus;
import com.workforcehub.enums.RoleType;
import com.workforcehub.exception.BadRequestException;
import com.workforcehub.exception.DuplicateResourceException;
import com.workforcehub.exception.ResourceNotFoundException;
import com.workforcehub.repository.EmployeeRepository;
import com.workforcehub.repository.RoleRepository;
import com.workforcehub.repository.UserRepository;
import com.workforcehub.security.JwtTokenProvider;
import com.workforcehub.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Authentication service handling login, registration, and token management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuditService auditService;

    /**
     * Authenticate user and return JWT tokens.
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        // Save refresh token
        User user = userRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", userPrincipal.getUsername()));
        user.setRefreshToken(refreshToken);
        user.resetFailedAttempts();
        userRepository.save(user);

        auditService.log(userPrincipal.getUsername(), "LOGIN", "User", user.getId(), "User logged in");

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpiration())
                .username(userPrincipal.getUsername())
                .email(userPrincipal.getEmail())
                .roles(userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Register a new user with EMPLOYEE role.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered");
        }

        Role employeeRole = roleRepository.findByName(RoleType.ROLE_EMPLOYEE)
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .roles(Set.of(employeeRole))
                .build();
        userRepository.save(user);

        // Generate employee ID
        long count = employeeRepository.countActive() + 1;
        String employeeId = String.format("EMP%03d", count);

        Employee employee = Employee.builder()
                .employeeId(employeeId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .joiningDate(LocalDate.now())
                .status(EmployeeStatus.ACTIVE)
                .user(user)
                .build();
        employeeRepository.save(employee);

        // Auto-login after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        auditService.log(user.getUsername(), "REGISTER", "User", user.getId(), "New user registered");

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpiration())
                .username(userPrincipal.getUsername())
                .email(userPrincipal.getEmail())
                .roles(userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Refresh access token using refresh token.
     */
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new BadRequestException("Refresh token mismatch");
        }

        String newAccessToken = tokenProvider.generateAccessToken(username);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpiration())
                .username(username)
                .email(user.getEmail())
                .build();
    }

    /**
     * Logout user by clearing refresh token.
     */
    @Transactional
    public void logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        user.setRefreshToken(null);
        userRepository.save(user);
        SecurityContextHolder.clearContext();
        auditService.log(username, "LOGOUT", "User", user.getId(), "User logged out");
    }

    /**
     * Initiate forgot password flow.
     */
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        userRepository.save(user);

        // In production, send email with reset link
        log.info("Password reset token generated for {}: {}", email, resetToken);
    }

    /**
     * Reset password using token.
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        userRepository.save(user);

        auditService.log(user.getUsername(), "PASSWORD_RESET", "User", user.getId(), "Password was reset");
    }
}
