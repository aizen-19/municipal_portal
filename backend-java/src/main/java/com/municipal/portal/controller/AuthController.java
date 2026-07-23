package com.municipal.portal.controller;

import com.municipal.portal.config.JwtUtil;
import com.municipal.portal.model.User;
import com.municipal.portal.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(
    origins = "*", 
    allowedHeaders = "*", 
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String MESSAGE = "message";
    private static final String EMAIL = "email";
    private static final String FULL_NAME = "fullName";

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthController(
            UserRepository userRepository,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // Salt password hashing utility to match Node logic
    private String hashPassword(String password) {
        try {
            String salt = "municipal_civic_portal_salt_2026";
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((password + salt).getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody Map<String, String> body) {
        String fullName = body.get(FULL_NAME);
        String email = body.get(EMAIL);
        String password = body.get("password");

        if (fullName == null || email == null || password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(MESSAGE, "All fields are required."));
        }

        Optional<User> existing = userRepository.findByEmailIgnoreCase(email);
        if (existing.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(MESSAGE, "Email is already registered."));
        }

        String passwordHash = hashPassword(password);
        String userId = "usr_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        User newUser = new User(userId, fullName, email.toLowerCase(), passwordHash, Instant.now().toString());

        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                MESSAGE, "User registered successfully!",
                "user", Map.of(
                        "id", newUser.getId(),
                        FULL_NAME, newUser.getFullName(),
                        EMAIL, newUser.getEmail()
                )
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String email = body.get(EMAIL);
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(MESSAGE, "Email and password are required."));
        }

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(MESSAGE, "Invalid email or password."));
        }

        User user = userOpt.get();
        String passwordHash = hashPassword(password);

        if (!user.getPasswordHash().equals(passwordHash)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(MESSAGE, "Invalid email or password."));
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getFullName());

        Map<String, Object> response = new HashMap<>();
        response.put(MESSAGE, "Login successful!");
        response.put("token", token);
        response.put("user", Map.of(
                "id", user.getId(),
                FULL_NAME, user.getFullName(),
                EMAIL, user.getEmail(),
                "registeredAt", user.getRegisteredAt()
        ));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(MESSAGE, "Unauthenticated."));
        }

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(MESSAGE, "User not found."));
        }

        User user = userOpt.get();

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                FULL_NAME, user.getFullName(),
                EMAIL, user.getEmail(),
                "registeredAt", user.getRegisteredAt()
        ));
    }
}