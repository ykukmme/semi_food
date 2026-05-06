package com.semi.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // V9 migration hash
        String storedHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        String password = "password123";
        
        System.out.println("Stored hash: " + storedHash);
        System.out.println("Testing password: " + password);
        System.out.println("Matches: " + encoder.matches(password, storedHash));
        
        // Generate new hash for password123
        String newHash = encoder.encode(password);
        System.out.println("New hash for password123: " + newHash);
        System.out.println("New hash matches: " + encoder.matches(password, newHash));
    }
}
