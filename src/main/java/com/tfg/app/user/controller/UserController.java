package com.tfg.app.user.controller;

import com.tfg.app.user.model.User;
import com.tfg.app.user.model.User.Role;
import com.tfg.app.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "API para la gestión de usuarios")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
        summary = "Obtener todos los usuarios", 
        description = "Devuelve la lista completa de usuarios del sistema"
    )
    @ApiResponse(responseCode = "200", description = "Usuarios encontrados")
    @GetMapping
    public List<User> all() {
        return userService.findAll();
    }

    @Operation(
        summary = "Obtener usuario por ID",
        description = "Devuelve un usuario específico según su ID"
    )
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return userService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Crear un nuevo usuario",
        description = "Registra un nuevo usuario en el sistema"
    )
    @ApiResponse(responseCode = "201", description = "Usuario creado")
    @ApiResponse(responseCode = "400", description = "Error en la solicitud, por ejemplo username duplicado")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody User u) {
        try {
            // Assign default CLIENT role if missing
            if (u.getRole() == null) {
                u.setRole(User.Role.CLIENT);
            }
            User saved = userService.save(u);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (DataIntegrityViolationException e) {
            // Check if it's a duplicate username error
            if (e.getMessage().contains("uk_") && e.getMessage().toLowerCase().contains("username")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ya existe un usuario con ese username");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }
            // Rethrow other database integrity errors
            throw e;
        }
    }

    @Operation(
        summary = "Actualizar usuario",
        description = "Actualiza la información de un usuario existente"
    )
    @ApiResponse(responseCode = "200", description = "Usuario actualizado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User u) {
        return userService.findById(id).map(ex -> {
            u.setId(id);
            return ResponseEntity.ok(userService.save(u));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Eliminar usuario",
        description = "Elimina un usuario del sistema por su ID"
    )
    @ApiResponse(responseCode = "204", description = "Usuario eliminado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return userService.findById(id).map(ex -> {
            userService.deleteById(id);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Asignar rol a usuario",
        description = "Asigna un rol específico a un usuario"
    )
    @ApiResponse(responseCode = "200", description = "Rol asignado")
    @PutMapping("/{id}/role")
    public ResponseEntity<User> addRole(@PathVariable Long id, @RequestBody User.Role role) {
        User updated = userService.assignRole(id, role);
        return ResponseEntity.ok(updated);
    }
}