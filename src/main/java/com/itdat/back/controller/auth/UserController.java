package com.itdat.back.controller.auth;

import com.itdat.back.entity.auth.User;
import com.itdat.back.service.auth.NaverWorksAuthService;
import com.itdat.back.service.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private NaverWorksAuthService naverWorksAuthService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        try {
            String token = userService.login(email, password);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 소셜 로그인 연동
     *
     * @param provider    소셜 로그인 제공자 (google, kakao, naver)
     * @param requestBody 소셜 로그인 데이터 (providerId, email, name)
     * @return JWT 토큰 또는 에러 메시지
     */
    @PostMapping("/oauth/{provider}")
    public ResponseEntity<?> handleSocialLogin(
            @PathVariable("provider") String provider,
            @RequestBody Map<String, String> requestBody) {
        String providerId = requestBody.get("providerId");
        String email = requestBody.get("email");
        String name = requestBody.get("name");

        try {
            // 소셜 로그인 처리
            String token = userService.socialLogin(provider, providerId, email, name);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "소셜 로그인 연동 실패: " + e.getMessage()));
        }
    }

    /**
     * 소셜 로그인 연동 해제
     *
     * @param provider    소셜 로그인 제공자 (google, kakao, naver)
     * @param requestBody 연동 해제 요청 데이터 (providerId, email)
     * @return 성공 메시지 또는 에러 메시지
     */
    @DeleteMapping("/oauth/{provider}")
    public ResponseEntity<?> unlinkSocialLogin(
            @PathVariable("provider") String provider,
            @RequestBody Map<String, String> requestBody) {
        String providerId = requestBody.get("providerId");
        String email = requestBody.get("email");

        try {
            userService.unlinkSocialLogin(provider, providerId, email);
            return ResponseEntity.ok(Map.of("message", "소셜 로그인 연동 해제 성공"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "소셜 로그인 연동 해제 실패: " + e.getMessage()));
        }
    }


    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        System.out.println("받은 유저 데이터: " + user.toString());
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
        try {
            userService.sendVerificationCode(email);
            return ResponseEntity.ok(Collections.singletonMap("message", "인증 코드가 성공적으로 발송 되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        }
    }


    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Boolean>> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        boolean isValid = userService.verifyCode(email, code);
        return ResponseEntity.ok(Collections.singletonMap("success", isValid));
    }

    @PostMapping("/callback")
    public ResponseEntity<?> handleCallback(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        try {
            String accessToken = naverWorksAuthService.fetchAccessToken(code);
            return ResponseEntity.ok(Collections.singletonMap("accessToken", accessToken));
        } catch (RuntimeException e) {
            System.err.println("Error fetching access token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Failed to fetch access token"));
        }
    }


}
