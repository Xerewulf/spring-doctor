package com.devtools.ai.service;

import com.devtools.ai.dto.AuthRequest;
import com.devtools.ai.dto.AuthResponse;
import com.devtools.ai.dto.RegisterRequest;
import com.devtools.ai.dto.UserDto;
import com.devtools.ai.entity.Role;
import com.devtools.ai.entity.Subscription;
import com.devtools.ai.entity.User;
import com.devtools.ai.exception.ApiException;
import com.devtools.ai.repository.RoleRepository;
import com.devtools.ai.repository.SubscriptionRepository;
import com.devtools.ai.repository.UserRepository;
import com.devtools.ai.security.CustomUserDetails;
import com.devtools.ai.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("An account with this email already exists", HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));

        User user = User.builder()
                .email(request.getEmail().trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .plan("FREE")
                .isActive(true)
                .roles(new HashSet<>(Collections.singletonList(userRole)))
                .build();

        User savedUser = userRepository.save(user);

        // Initialize free subscription
        Subscription subscription = Subscription.builder()
                .user(savedUser)
                .plan("FREE")
                .status("ACTIVE")
                .build();
        subscriptionRepository.save(subscription);

        String token = tokenProvider.generateToken(savedUser.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(mapToUserDto(savedUser))
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail().trim().toLowerCase(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            String token = tokenProvider.generateToken(user.getEmail());

            return AuthResponse.builder()
                    .token(token)
                    .user(mapToUserDto(user))
                    .build();
        } catch (BadCredentialsException ex) {
            throw new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS");
        }
    }

    @Transactional(readOnly = true)
    public User getCurrentUserOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userRepository.findById(userDetails.getUser().getId()).orElse(null);
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    public UserDto mapToUserDto(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .plan(user.getPlan())
                .roles(roles)
                .build();
    }
}
