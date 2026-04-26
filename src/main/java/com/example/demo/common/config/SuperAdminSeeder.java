package com.example.demo.common.config;

import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile("seed")
@RequiredArgsConstructor
public class SuperAdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        String email = "superadmin@puskesmas.id";
        if (userRepository.existsByEmailAndDeletedAtIsNull(email)) {
            log.info("Super admin already exists");
            return;
        }

        User superAdmin = new User();
        superAdmin.setEmail(email);
        superAdmin.setPasswordHash(passwordEncoder.encode("SuperAdmin123!"));
        superAdmin.setFullName("Super Administrator");
        userRepository.save(superAdmin);

        log.info("Super admin seeded: {}", email);
    }
}
