package org.ratha.virtualbookstore.config;

import org.ratha.virtualbookstore.model.User;
import org.ratha.virtualbookstore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner createDefaultAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            userRepository.findByUsername("admin").ifPresentOrElse(
                    user -> System.out.println("Admin user already exists"),
                    () -> {
                        User admin = new User();
                        admin.setUsername("admin");
                        admin.setPassword(passwordEncoder.encode("admin123"));
                        admin.setRole("ROLE_ADMIN");
                        userRepository.save(admin);
                        System.out.println("Default admin user created.");
                    }
            );
        };
    }
}
