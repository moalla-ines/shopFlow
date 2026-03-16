package com.example.shopFlow.service;

import com.example.shopFlow.dto.request.LoginRequest;
import com.example.shopFlow.dto.request.RefreshTokenRequest;
import com.example.shopFlow.dto.request.RegisterRequest;
import com.example.shopFlow.dto.response.AuthResponse;
import com.example.shopFlow.entity.Cart;
import com.example.shopFlow.entity.Users;
import com.example.shopFlow.entity.Users;
import com.example.shopFlow.entity.enums.Role;
import com.example.shopFlow.repository.CartRepository;
import com.example.shopFlow.repository.UserRepository;
import com.example.shopFlow.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    // ─── Inscription ──────────────────────────────────────────
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé : " + request.getEmail());
        }

        Users user = Users.builder()
                .prenom(request.getPrenom())
                .nom(request.getNom())
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(request.getRole() != null ? request.getRole() : Role.CUSTOMER)
                .actif(true)
                .build();

        userRepository.save(user);

        // Créer un panier automatiquement pour les CUSTOMER
        if (user.getRole() == Role.CUSTOMER) {
            Cart cart = Cart.builder()
                    .customer(user)
                    .build();
            cartRepository.save(cart);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    // ─── Connexion ────────────────────────────────────────────
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getMotDePasse()
                )
        );

        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    // ─── Refresh token ────────────────────────────────────────
    public AuthResponse refresh(RefreshTokenRequest request) {
        String email = jwtUtils.extractEmail(request.getRefreshToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtUtils.isTokenValid(request.getRefreshToken(), userDetails)) {
            throw new RuntimeException("Refresh token invalide ou expiré");
        }

        String newAccessToken = jwtUtils.generateAccessToken(userDetails);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .email(email)
                .build();
    }
}