package com.workforcehub.controller;

import com.workforcehub.dto.request.LoginRequest;
import com.workforcehub.dto.request.RegisterRequest;
import com.workforcehub.dto.response.ApiResponse;
import com.workforcehub.dto.response.AuthResponse;
import com.workforcehub.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication & Authorization APIs")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return JWT tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse auth = authService.login(request);
        Cookie cookie = new Cookie("jwt", auth.getAccessToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(900);
        response.addCookie(cookie);
        return ResponseEntity.ok(ApiResponse.success("Login successful", auth));
    }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register a new user")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse auth = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", auth));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh Token", description = "Refresh access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestBody Map<String, String> request) {
        AuthResponse auth = authService.refreshToken(request.get("refreshToken"));
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", auth));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout and invalidate tokens")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) {
        authService.logout(userDetails.getUsername());
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot Password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody Map<String, String> request) {
        authService.forgotPassword(request.get("email"));
        return ResponseEntity.ok(ApiResponse.success("Password reset email sent", null));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody Map<String, String> request) {
        authService.resetPassword(request.get("token"), request.get("newPassword"));
        return ResponseEntity.ok(ApiResponse.success("Password reset successful", null));
    }
}
