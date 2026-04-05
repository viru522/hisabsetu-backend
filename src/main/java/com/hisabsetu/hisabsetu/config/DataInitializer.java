package com.hisabsetu.hisabsetu.config;

import com.hisabsetu.hisabsetu.entity.Role;
import com.hisabsetu.hisabsetu.entity.User;

import com.hisabsetu.hisabsetu.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {

        // 🔥 CREATE FIRST ADMIN IF NOT EXISTS
        if (userRepo.findByUsername("admin").isEmpty()) {

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin123"));
            admin.setRole(Role.ROLE_ADMIN);

            userRepo.save(admin);

//            System.out.println("🔥 Default admin created: admin / admin123");
        }
    }
}