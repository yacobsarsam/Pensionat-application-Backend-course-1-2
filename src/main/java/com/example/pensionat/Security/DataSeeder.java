package com.example.pensionat.Security;

import com.example.pensionat.Security.Models.Role;
import com.example.pensionat.Security.Models.User;
import com.example.pensionat.Security.Repositories.RoleRepository;
import com.example.pensionat.Security.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.List;


@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        User admin = User.builder()
                .username("Olof")
                .password(passwordEncoder.encode("asdasd123"))
                .email("admin@example.com")
                .build();
        userRepository.save(admin);

        User receptionist = User.builder()
                .username("Linus")
                .password(passwordEncoder.encode("asdasd123"))
                .email("receptionist@example.com")
                .build();
        userRepository.save(receptionist);

        User Batman = User.builder()
                .username("Bruce Wayne")
                .password(passwordEncoder.encode("asdasd123"))
                .email("bothRoles@example.com")
                .build();
        userRepository.save(Batman);


        Role adminRole = Role.builder().role("ADMIN").build();
        roleRepository.save(adminRole);

        Role receptionistRole = Role.builder().role("RECEPTIONIST").build();
        roleRepository.save(receptionistRole);

        admin.setRoles(new HashSet<>(List.of(adminRole)));
        receptionist.setRoles(new HashSet<>(List.of(receptionistRole)));
        Batman.setRoles(new HashSet<>(List.of(adminRole,receptionistRole)));

        userRepository.save(admin);
        userRepository.save(receptionist);
        userRepository.save(Batman);
    }
}