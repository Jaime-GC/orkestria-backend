package com.tfg.app.user.service;

import com.tfg.app.user.model.User;
import com.tfg.app.user.repository.RoleRepository;
import com.tfg.app.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock UserRepository userRepo;
    @Mock RoleRepository roleRepo;
    @InjectMocks UserService svc;

    @BeforeEach void init() {
        MockitoAnnotations.openMocks(this);
        svc = new UserService(userRepo, roleRepo, new BCryptPasswordEncoder());
    }

    @Test void register_savesUserWithEncodedPassword(){
        User u = new User(null,"bob","b@b","pass",null);
        when(roleRepo.findAll()).thenReturn(List.of());
        when(roleRepo.save(any())).thenAnswer(i->i.getArgument(0));
        when(userRepo.save(any())).thenAnswer(i->i.getArgument(0));
        User saved = svc.register(u);
        Assertions.assertNotEquals("pass", saved.getPassword());
        verify(userRepo).save(saved);
    }

    @Test void findByUsername_returnsOptional(){
        when(userRepo.findByUsername("x")).thenReturn(Optional.of(new User()));
        Assertions.assertTrue(svc.findByUsername("x").isPresent());
    }
}
