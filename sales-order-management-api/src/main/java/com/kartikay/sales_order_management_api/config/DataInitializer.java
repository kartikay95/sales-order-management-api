package com.kartikay.sales_order_management_api.config;


import com.kartikay.sales_order_management_api.domain.Role;
import com.kartikay.sales_order_management_api.domain.User;
import com.kartikay.sales_order_management_api.repository.RoleRepository;
import com.kartikay.sales_order_management_api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(RoleRepository roleRepo, UserRepository userRepo, PasswordEncoder encoder) {
        return args -> {
            if (roleRepo.findByName("ROLE_ADMIN").isEmpty()) {
                roleRepo.save(new Role(null, "ROLE_ADMIN"));
            }
            if (roleRepo.findByName("ROLE_USER").isEmpty()) {
                roleRepo.save(new Role(null, "ROLE_USER"));
            }

            if (userRepo.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("adminpass"));
                Role adminRole = roleRepo.findByName("ROLE_ADMIN").orElseThrow();
                admin.getRoles().add(adminRole);
                userRepo.save(admin);
                System.out.println("Seeded admin/adminpass (change immediately)");
            }
        };
    }
}
