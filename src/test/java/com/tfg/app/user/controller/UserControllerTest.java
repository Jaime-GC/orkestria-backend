package com.tfg.app.user.controller;

import com.tfg.app.user.model.User;
import com.tfg.app.user.model.User.Role;
import com.tfg.app.user.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock UserService userService;
    @InjectMocks UserController controller;

    @Test void all_returnsList() {
        List<User> list = List.of(new User(), new User());
        when(userService.findAll()).thenReturn(list);
        List<User> result = controller.all();
        assertEquals(2, result.size());
    }

    @Test void getById_found() {
        User u = new User(); u.setId(1L);
        when(userService.findById(1L)).thenReturn(Optional.of(u));
        ResponseEntity<User> resp = controller.getById(1L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1L, resp.getBody().getId());
    }

    @Test void getById_notFound() {
        when(userService.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<User> resp = controller.getById(1L);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test void create_returnsCreated() {
        User u = new User(); u.setId(2L);
        when(userService.save(any())).thenReturn(u);
        
        ResponseEntity<?> resp = controller.create(new User());
        
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertTrue(resp.getBody() instanceof User);
        assertEquals(2L, ((User)resp.getBody()).getId());
    }
    
    @Test void create_handlesDuplicateUsername() {
        when(userService.save(any())).thenThrow(
            new DataIntegrityViolationException("ERROR: uk_username_123 llave duplicada viola restricción de unicidad")
        );
        
        ResponseEntity<?> resp = controller.create(new User());
        
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertTrue(resp.getBody() instanceof Map);
        assertTrue(((Map<?,?>)resp.getBody()).containsKey("error"));
    }

    @Test void update_found() {
        User existing = new User(); existing.setId(3L);
        User updated = new User(); updated.setId(3L);
        when(userService.findById(3L)).thenReturn(Optional.of(existing));
        when(userService.save(any())).thenReturn(updated);
        ResponseEntity<User> resp = controller.update(3L, new User());
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(3L, resp.getBody().getId());
    }

    @Test void update_notFound() {
        when(userService.findById(4L)).thenReturn(Optional.empty());
        ResponseEntity<User> resp = controller.update(4L, new User());
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test void delete_found() {
        User u = new User(); u.setId(5L);
        when(userService.findById(5L)).thenReturn(Optional.of(u));
        ResponseEntity<Void> resp = controller.delete(5L);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    @Test void delete_notFound() {
        when(userService.findById(6L)).thenReturn(Optional.empty());
        ResponseEntity<Void> resp = controller.delete(6L);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test void addRole_setsRole() {
        User u = new User(1L,"bob","b@b", null);
        when(userService.assignRole(1L, Role.CLIENT)).thenReturn(u);

        var resp = controller.addRole(1L, Role.CLIENT);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(userService).assignRole(1L, Role.CLIENT);
    }
}
