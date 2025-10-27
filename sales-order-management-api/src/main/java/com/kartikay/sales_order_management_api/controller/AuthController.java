package com.kartikay.sales_order_management_api.controller;

import com.kartikay.sales_order_management_api.domain.Role;
import com.kartikay.sales_order_management_api.domain.User;
import com.kartikay.sales_order_management_api.dto.AuthRequest;
import com.kartikay.sales_order_management_api.dto.AuthResponse;
import com.kartikay.sales_order_management_api.dto.RegisterRequest;
import com.kartikay.sales_order_management_api.repository.RoleRepository;
import com.kartikay.sales_order_management_api.repository.UserRepository;
import com.kartikay.sales_order_management_api.security.JwtTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtUtil;

    public AuthController(AuthenticationManager authManager, UserRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                          JwtTokenUtil jwtUtil) {
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest req) {
        if (userRepository.findByUsername(req.username()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already taken");
        }

        User user = new User();
        user.setUsername(req.username());
        user.setPassword(passwordEncoder.encode(req.password()));

        // assigning ROLE_USER by default
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found. Seed roles first."));
        user.getRoles().add(userRole);

        userRepository.save(user);
        return ResponseEntity.ok("User created");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.username(), req.password())
            );

            // build token
            User user = userRepository.findByUsername(req.username()).orElseThrow();
            var roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
            String token = jwtUtil.generateToken(user.getUsername(), roles);
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).build();
        }
    }
}

