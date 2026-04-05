package com.hisabsetu.hisabsetu.controller;

import com.hisabsetu.hisabsetu.entity.User;
import com.hisabsetu.hisabsetu.entity.UserSession;
import com.hisabsetu.hisabsetu.repository.UserRepository;
import com.hisabsetu.hisabsetu.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepo;

    // REGISTER
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return authService.register(user);
    }

    // ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/create")
    public String createAdmin(@RequestBody User user) {

        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
return null;
    }


    // 🔥 LOGIN
    @PostMapping("/login")
    public Map<String, String> login(
            @RequestBody User user,
            HttpServletRequest request
    ) {
        return authService.login(user, request);
    }


    // 🔥 REFRESH
    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody Map<String, String> req) {
        return authService.refresh(req.get("refreshToken"));
    }

    @PostMapping("/logout-device")
    public String logoutDevice(@RequestBody Map<String, String> req) {

        authService.logoutDevice(req.get("refreshToken"));

        return "Device logged out";
    }

    // 🔥 LOGOUT
    @PostMapping("/logout")
    public String logout(@RequestBody Map<String, String> req) {
        authService.logout(req.get("refreshToken"));
        return "Logged out successfully";
    }
}