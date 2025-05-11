package com.tfg.app.user.service;

import com.tfg.app.user.model.User;
import com.tfg.app.user.model.User.Role;
import com.tfg.app.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    @Mock UserRepository userRepo;
    @InjectMocks UserService svc;

    @BeforeEach void init() { MockitoAnnotations.openMocks(this); }

    @Test void findAll_shouldReturnList() {
        var list = List.of(new User(), new User());
        when(userRepo.findAll()).thenReturn(list);
        assertEquals(2, svc.findAll().size());
        verify(userRepo).findAll();
    }

    @Test void findById_whenExists() {
        var u = new User(); u.setId(1L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(u));
        var opt = svc.findById(1L);
        assertTrue(opt.isPresent());
        assertEquals(1L, opt.get().getId());
    }

    @Test void save_shouldCallSave() {
        var u = new User(null, "bob", "b@b", Role.EMPLOYEE);
        when(userRepo.save(u)).thenReturn(u);

        User result = svc.save(u);
        assertEquals(u, result);
        verify(userRepo, times(1)).save(u);
    }

    @Test void deleteById_shouldCallDelete() {
        svc.deleteById(5L);
        verify(userRepo).deleteById(5L);
    }

    @Test void assignRole_setsSingleRole() {
        var u = new User(1L,"bob","b@b", null);
        when(userRepo.findById(1L)).thenReturn(Optional.of(u));
        when(userRepo.save(u)).thenReturn(u);

        User res = svc.assignRole(1L, Role.EMPLOYEE);
        assertEquals(Role.EMPLOYEE, res.getRole());
        verify(userRepo).save(u);
    }
}
