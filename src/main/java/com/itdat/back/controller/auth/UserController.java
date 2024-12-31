package com.itdat.back.controller.auth;

import com.itdat.back.entity.auth.ProviderType;
import com.itdat.back.entity.auth.User;

import com.itdat.back.entity.auth.UserType;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.service.auth.NaverWorksAuthService;
import com.itdat.back.service.auth.UserService;
import com.itdat.back.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;


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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

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
        System.out.println("test");
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        System.out.println(email+password);
        try {
            String token = userService.login(email, password);
            System.out.println("토큰발급" + token);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }


    /**
     * 사용자 회원가입
     *
     * @param user 요청 바디(User 객체): 사용자가 입력한 회원가입 정보
     * @return 등록된 사용자(User 객체): 새로 생성된 사용자의 정보 반환
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
//        System.out.println("받은 유저 데이터: " + user.toString());
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }


    @PostMapping("/social/register")
    public ResponseEntity<?> registerSocialUser(@RequestBody Map<String, String> requestBody) {
        try {
            String userId = requestBody.get("userId");
            String userName = requestBody.get("userName");
            String password = requestBody.get("password");
            String userPhone = requestBody.get("userPhone");
            String userEmail = requestBody.get("userEmail");
            LocalDate userBirth = LocalDate.parse(requestBody.get("userBirth"));
            ProviderType providerType = ProviderType.valueOf(requestBody.get("providerType"));

            if (userRepository.findByUserEmail(userEmail) != null) {
                throw new IllegalStateException("이미 가입된 이메일입니다.");
            }

            // 신규 사용자 생성
            User newUser = new User();
            newUser.setUserId(userId);
            newUser.setUserName(userName);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setUserPhone(userPhone);
            newUser.setUserEmail(userEmail);
            newUser.setUserBirth(userBirth);
            newUser.setProviderType(providerType);
            newUser.setUserType(UserType.PERSONAL);

            userRepository.save(newUser);

            String token = jwtTokenUtil.generateToken(userEmail);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "회원가입 실패: " + e.getMessage()));
        }
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


}
