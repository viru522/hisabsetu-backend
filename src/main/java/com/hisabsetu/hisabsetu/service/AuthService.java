package com.hisabsetu.hisabsetu.service;

import com.hisabsetu.hisabsetu.config.jwt.JwtUtil;
import com.hisabsetu.hisabsetu.entity.RefreshToken;
import com.hisabsetu.hisabsetu.entity.Role;
import com.hisabsetu.hisabsetu.entity.User;
import com.hisabsetu.hisabsetu.entity.UserSession;
import com.hisabsetu.hisabsetu.repository.RefreshTokenRepository;
import com.hisabsetu.hisabsetu.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshRepo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;

    // 🔥 REGISTER
    public String register(User user) {

        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        userRepo.save(user);
        return "User registered successfully";
    }


    // 🔥 LOGIN
    public Map<String, String> login(User user, HttpServletRequest request) {

        User dbUser = userRepo.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(user.getPassword(), dbUser.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String accessToken = jwtUtil.generateAccessToken(
                dbUser.getUsername(),
                dbUser.getRole()
        );

        String refreshToken = jwtUtil.generateRefreshToken(
                dbUser.getUsername()
        );

        saveRefreshToken(dbUser.getUsername(), refreshToken);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }


    // 🔥 REFRESH (WITH SESSION UPDATE)
    public Map<String, String> refresh(String oldToken) {

        RefreshToken stored = refreshRepo.findByToken(oldToken)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (stored.isRevoked()) {
            throw new RuntimeException("Token revoked");
        }

        if (stored.getExpiryDate().before(new Date())) {
            throw new RuntimeException("Token expired");
        }

        // revoke old token
        stored.setRevoked(true);
        refreshRepo.save(stored);

        String username = stored.getUsername();

        User user = userRepo.findByUsername(username)
                .orElseThrow();

        String newAccess = jwtUtil.generateAccessToken(username, user.getRole());
        String newRefresh = jwtUtil.generateRefreshToken(username);

        saveRefreshToken(username, newRefresh);

        return Map.of(
                "accessToken", newAccess,
                "refreshToken", newRefresh
        );
    }

    // 🔥 LOGOUT SINGLE
    public void logout(String refreshToken) {

        RefreshToken stored = refreshRepo.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        stored.setRevoked(true);
        refreshRepo.save(stored);

    }



    // 🔥 LOGOUT ALL
    public void logoutAll(String username) {


        List<RefreshToken> tokens = refreshRepo.findByUsername(username);
        tokens.forEach(t -> t.setRevoked(true));
        refreshRepo.saveAll(tokens);
    }

    // 🔥 LOGOUT DEVICE
    public void logoutDevice(String refreshToken) {


        RefreshToken token = refreshRepo.findByToken(refreshToken)
                .orElseThrow();

        token.setRevoked(true);
        refreshRepo.save(token);
    }

    // 🔥 HELPER
    private void saveRefreshToken(String username, String token) {

        RefreshToken entity = new RefreshToken();
        entity.setToken(token);
        entity.setUsername(username);
        entity.setExpiryDate(
                new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)
        );
        entity.setRevoked(false);

        refreshRepo.save(entity);
    }
}