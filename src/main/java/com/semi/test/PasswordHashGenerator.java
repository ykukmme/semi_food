package com.semi.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * BCrypt hash generator for testing
 * This will generate the correct hash for 'admin123'
 */
public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Generate hash for 'admin123'
        String password = "admin123";
        String hash = encoder.encode(password);
        
        System.out.println("Password: " + password);
        System.out.println("Generated Hash: " + hash);
        System.out.println("Hash Length: " + hash.length());
        
        // Test the hash
        boolean matches = encoder.matches(password, hash);
        System.out.println("Hash matches: " + matches);
        
        // Test with common hashes
        String[] commonHashes = {
            "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
            "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi"
        };
        
        for (String testHash : commonHashes) {
            boolean testMatches = encoder.matches(password, testHash);
            System.out.println("Hash " + testHash.substring(0, 20) + "... matches: " + testMatches);
        }
    }
}
