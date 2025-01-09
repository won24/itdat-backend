package com.itdat.back.controller.auth;

import com.itdat.back.entity.auth.ProviderType;
import com.itdat.back.entity.auth.Role;
import com.itdat.back.entity.auth.User;

import com.itdat.back.entity.auth.UserType;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.service.auth.NaverWorksAuthService;
import com.itdat.back.service.auth.UserService;
import com.itdat.back.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * 작성자 : 김동규
 *
 * 작성일 : 2024-12-20
 *
 * 사용자 인증 및 회원가입 로직 처리 구현
 * */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NaverWorksAuthService naverWorksAuthService;

    /**
     * 사용자 로그인
     *
     * @param loginRequest 요청 바디(Map 형식):
     *                     - email: 사용자의 이메일
     *                     - password: 사용자의 비밀번호
     * @return JWT 토큰(Map 형식):
     *         - token: 인증된 사용자의 JWT 토큰
     * @throws HttpStatus.UNAUTHORIZED: 인증 실패 시 오류 메시지 반환
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        System.out.println("로그인요청"+email+password);
        try {
            String token = userService.login(email, password);
            System.out.println("토큰발급" + token);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 로그아웃 엔드포인트
     *
     * @param token Authorization 헤더에 포함된 JWT 토큰
     * @return 로그아웃 결과 메시지(String 형식): "로그아웃 성공" 또는 오류 메시지
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("토큰이 제공되지 않았습니다.");
        }

        // JWT는 상태를 저장하지 않으므로 클라이언트에서만 토큰을 삭제
        // 추가적으로 토큰을 블랙리스트에 저장하려면 여기에서 처리하면 될 듯 ?

//        System.out.println("로그아웃 요청 처리됨. 토큰: " + token);

        // 응답 반환
        return ResponseEntity.ok("로그아웃 성공");
    }


    /**
     * 사용자 회원가입
     *
     * @param user 요청 바디(User 객체): 사용자가 입력한 회원가입 정보
     * @return 등록된 사용자(User 객체): 새로 생성된 사용자의 정보 반환
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        System.out.println("받은 유저 데이터: " + user.toString());
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    /**
     * 현재 사용자 정보 조회
     *
     * @param email 인증된 사용자의 이메일
     * @return 사용자 정보(Map 형식):
     *         - email: 사용자 이메일
     *         - name: 사용자 이름
     *         - isSocialUser: 소셜 로그인 여부
     * @throws HttpStatus.UNAUTHORIZED: 인증되지 않은 사용자
     * @throws HttpStatus.NOT_FOUND: 사용자 정보 없음
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal String email) {
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        User user = userRepository.findByUserEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("email", user.getUserEmail());
        response.put("name", user.getUserName());
        response.put("isSocialUser", user.getProviderType() != null); // 소셜 로그인 여부 확인

        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 ID 또는 이메일 가용성 확인
     *
     * @param type 쿼리 파라미터(String): 확인하려는 유형(userId 또는 userEmail)
     * @param value 쿼리 파라미터(String): 확인하려는 값
     * @return 가용성(Map 형식):
     *         - available: true(사용 가능) 또는 false(이미 존재)
     */
    @GetMapping("/check-availability")
    public ResponseEntity<Map<String, Boolean>> checkAvailability(
            @RequestParam("type") String type,
            @RequestParam("value") String value) {

//        System.out.println("받은 유저 type: " + type);
//        System.out.println("받은 유저 value: " + value);

        boolean isAvailable = false;

        if ("userId".equals(type)) {
            isAvailable = userService.isUserIdAvailable(value);
        } else if ("userEmail".equals(type)) {
            isAvailable = userService.isUserEmailAvailable(value);
        }

        return ResponseEntity.ok(Collections.singletonMap("available", isAvailable));
    }

    /**
     * 이메일 인증 코드 발송
     *
     * @param request 요청 바디(Map 형식):
     *                - email: 인증 코드를 받을 이메일
     * @return 성공 메시지(Map 형식):
     *         - message: 인증 코드 발송 성공 메시지
     * @throws HttpStatus.BAD_REQUEST: 잘못된 이메일 요청
     */
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


    /**
     * 인증 코드 검증
     *
     * @param request 요청 바디(Map 형식):
     *                - email: 인증 코드 발송 대상 이메일
     *                - code: 사용자가 입력한 인증 코드
     * @return 검증 결과(Map 형식):
     *         - success: true(인증 성공) 또는 false(인증 실패)
     */
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Boolean>> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        boolean isValid = userService.verifyCode(email, code);
        return ResponseEntity.ok(Collections.singletonMap("success", isValid));
    }

    /**
     * 네이버 인증 콜백 처리
     *
     * @param request 요청 바디(Map 형식):
     *                - code: 네이버 인증 서버에서 전달받은 인증 코드
     * @return 엑세스 토큰(Map 형식):
     *         - accessToken: 네이버 인증을 통해 발급된 엑세스 토큰
     * @throws HttpStatus.INTERNAL_SERVER_ERROR: 엑세스 토큰 발급 실패
     */
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

    /**
     * 이메일로 사용자 정보 조회
     *
     * @param email 쿼리 파라미터(String): 조회할 사용자의 이메일
     * @return 사용자 정보(Map 형식):
     *         - userName: 사용자 이름
     *         - companyName: 회사 이름
     * @throws HttpStatus.NOT_FOUND: 사용자 정보 없음
     */
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfoByEmail(@RequestParam String email) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUserEmail(email));
        if (user.isPresent()) {
            User foundUser = user.get();
            Map<String, String> response = new HashMap<>();
            response.put("userName", foundUser.getUserName());
            response.put("companyName", foundUser.getCompany());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
    }


}
