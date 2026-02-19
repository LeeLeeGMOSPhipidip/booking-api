package com.lee.booking.bookin_api.controller;

import com.lee.booking.bookin_api.dto.AuthRequests;
import com.lee.booking.bookin_api.entity.UserEntity;
import com.lee.booking.bookin_api.repository.UserRepository;
import com.lee.booking.bookin_api.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody AuthRequests.RegisterRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        if (userRepo.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        String hash = passwordEncoder.encode(req.getPassword());
        userRepo.save(new UserEntity(email, hash));
    }

    @PostMapping("/login")
    public AuthRequests.AuthResponse login(@Valid @RequestBody AuthRequests.LoginRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        var user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthRequests.AuthResponse(token);
    }
}
