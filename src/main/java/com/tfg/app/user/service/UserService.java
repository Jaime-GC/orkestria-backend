package com.tfg.app.user.service;

import com.tfg.app.user.model.Role;
import com.tfg.app.user.model.User;
import com.tfg.app.user.model.Role.RoleName;
import com.tfg.app.user.repository.RoleRepository;
import com.tfg.app.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo,
                       RoleRepository roleRepo,
                       PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role role = roleRepo.findAll().stream()
                    .filter(r -> r.getName() == RoleName.CLIENT)
                    .findFirst()
                    .orElseGet(() -> roleRepo.save(new Role(null, RoleName.CLIENT)));
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
        }
        return userRepo.save(user);
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public User assignRole(Long userId, RoleName roleName) {
        User u = userRepo.findById(userId).orElseThrow();
        Role role = roleRepo.findAll().stream()
                .filter(r -> r.getName() == roleName)
                .findFirst()
                .orElseGet(() -> roleRepo.save(new Role(null, roleName)));
        u.getRoles().add(role);
        return userRepo.save(u);
    }
}
