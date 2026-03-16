package com.example.shopFlow.controller;

import com.example.shopFlow.dto.request.LoginRequest;
import com.example.shopFlow.dto.request.RefreshTokenRequest;
import com.example.shopFlow.dto.request.RegisterRequest;
import com.example.shopFlow.dto.response.AuthResponse;
import com.example.shopFlow.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ─── Inscription ──────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    // ─── Connexion ────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ─── Refresh token ────────────────────────────────────────
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    // ─── Logout ───────────────────────────────────────────────
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // Côté serveur stateless — le client supprime le token
        return ResponseEntity.ok("Déconnexion réussie");
    }
}