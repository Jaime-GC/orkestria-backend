package com.tfg.app.user.controller;

import com.tfg.app.user.model.User;
import com.tfg.app.user.model.Role.RoleName;
import com.tfg.app.user.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService svc;
    public UserController(UserService s){this.svc=s;}

    @GetMapping
    public List<User> all() { 
        return svc.findAll();            
    }

    @PutMapping("/{id}/roles")
    public User addRole(@PathVariable Long id, @RequestBody RoleName rn) {
        return svc.assignRole(id, rn);
    }
}
