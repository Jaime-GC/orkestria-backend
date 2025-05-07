package com.tfg.app.auth.controller;

import com.tfg.app.user.model.User;
import com.tfg.app.user.service.AuthService;
import com.tfg.app.user.service.UserService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authSvc;
    private final UserService userSvc;
    public AuthController(AuthService a, UserService u){this.authSvc=a;this.userSvc=u;}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User u){
        return ResponseEntity.ok(userSvc.register(u));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest r){
        String token = authSvc.login(r.getUsername(),r.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Data static class AuthRequest { private String username,password; }
    @Data @AllArgsConstructor static class AuthResponse { private String token; }
}
