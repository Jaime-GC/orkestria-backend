package com.tfg.app.user.controller;

import com.tfg.app.user.model.User;
import com.tfg.app.user.model.User.Role;
import com.tfg.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> all() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return userService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User u) {
        if (u.getRoles() == null) {
            u.setRoles(new java.util.HashSet<>());
        }
        User saved = userService.save(u);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User u) {
        return userService.findById(id).map(ex -> {
            u.setId(id);
            return ResponseEntity.ok(userService.save(u));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return userService.findById(id).map(ex -> {
            userService.deleteById(id);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<User> addRole(@PathVariable Long id, @RequestBody Role role) {
        User updated = userService.assignRole(id, role);
        return ResponseEntity.ok(updated);
    }
}