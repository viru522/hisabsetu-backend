package com.hisabsetu.hisabsetu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class UserConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new RuntimeException("Use JWT authentication");
        };
    }
}