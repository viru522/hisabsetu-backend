package com.hisabsetu.hisabsetu.controller;

import com.hisabsetu.hisabsetu.entity.Role;
import com.hisabsetu.hisabsetu.entity.User;
import com.hisabsetu.hisabsetu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    // 🔥 GET ALL USERS
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    // 🔥 PROMOTE USER → ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/promote/{id}")
    public String promoteToAdmin(@PathVariable Long id) {

        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(Role.ROLE_ADMIN);
        userRepo.save(user);

        return "User promoted to admin";
    }

    // 🔥 DELETE USER
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {

        userRepo.deleteById(id);

        return "User deleted";
    }
}