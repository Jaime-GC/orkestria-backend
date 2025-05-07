package com.tfg.app.user.service;

import com.tfg.app.auth.security.JwtTokenProvider;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    @Mock AuthenticationManager authMgr;
    @Mock JwtTokenProvider jwt;
    private AuthService svc;                   // updated

    @BeforeEach void init(){
        MockitoAnnotations.openMocks(this);    // added
        svc = new AuthService(authMgr, jwt);
    }

    @Test void login_returnsToken(){
        doNothing().when(authMgr).authenticate(any());
        when(jwt.generateToken("u")).thenReturn("tok");
        String t = svc.login("u","p");
        assertEquals("tok", t);
    }
}
