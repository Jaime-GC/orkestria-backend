package com.tfg.app.user.service;

import com.tfg.app.auth.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtProvider;

    public AuthService(AuthenticationManager authManager, JwtTokenProvider jwtProvider) {
        this.authManager = authManager;
        this.jwtProvider = jwtProvider;
    }

    public String login(String username, String password) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return jwtProvider.generateToken(username);
    }

    public String refreshToken(String token) {
        String user = jwtProvider.getUsernameFromJWT(token);
        return jwtProvider.generateToken(user);
    }
}
