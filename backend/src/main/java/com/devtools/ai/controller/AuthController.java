package com.devtools.ai.controller;

import com.devtools.ai.dto.ApiResponse;
import com.devtools.ai.dto.AuthRequest;
import com.devtools.ai.dto.AuthResponse;
import com.devtools.ai.dto.RegisterRequest;
import com.devtools.ai.dto.UserDto;
import com.devtools.ai.entity.User;
import com.devtools.ai.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(ApiResponse.ok(response, "Registration successful"), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Login successful"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        User user = authService.getCurrentUserOrNull();
        if (user == null) {
            return new ResponseEntity<>(ApiResponse.error("Not authenticated", "UNAUTHORIZED"), HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(ApiResponse.ok(authService.mapToUserDto(user)));
    }
}
