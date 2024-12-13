package com.itdat.back.controller.auth;

import com.itdat.back.entity.auth.User;
import com.itdat.back.service.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    @GetMapping("/check-availability")
    public ResponseEntity<Map<String, Boolean>> checkAvailability(
            @RequestParam("type") String type,
            @RequestParam("value") String value) {
        boolean isAvailable = false;

        if ("userId".equals(type)) {
            isAvailable = userService.isUserIdAvailable(value);
        } else if ("userEmail".equals(type)) {
            isAvailable = userService.isUserEmailAvailable(value);
        }

        return ResponseEntity.ok(Collections.singletonMap("available", isAvailable));
    }

    @PostMapping("/send-verification-code")
    public ResponseEntity<Map<String, String>> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean isSent = userService.sendVerificationCode(email);
        if (isSent) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Verification code sent successfully."));
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Failed to send verification code."));
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Boolean>> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        boolean isValid = userService.verifyCode(email, code);
        return ResponseEntity.ok(Collections.singletonMap("success", isValid));
    }

}
