package com.itdat.back.controller.auth;

import com.itdat.back.entity.auth.ProviderType;
import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.auth.UserType;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth")
public class SocialAuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 소셜 사용자 회원가입
     *
     * @param requestBody 요청 바디(Map 형식):
     *                     - userId: 소셜 로그인 고유 ID
     *                     - userName: 사용자 이름
     *                     - password: 사용자 비밀번호
     *                     - userPhone: 사용자 전화번호
     *                     - userEmail: 사용자 이메일
     *                     - userBirth: 사용자 생년월일(yyyy-MM-dd 형식)
     *                     - providerType: 소셜 제공자(GOOGLE, NAVER, KAKAO)
     * @return JWT 토큰(Map 형식):
     *         - token: 등록된 소셜 사용자의 JWT 토큰
     * @throws HttpStatus.BAD_REQUEST: 잘못된 입력 데이터
     * @throws HttpStatus.CONFLICT: 중복된 이메일
     * @throws HttpStatus.INTERNAL_SERVER_ERROR: 시스템 오류
     */
    @PostMapping("/{provider}")
    public ResponseEntity<?> registerSocialUser(@PathVariable String provider, @RequestBody Map<String, String> requestBody) {
        try {
            // 요청에서 필수 데이터 추출
            String userId = requestBody.get("userId");
            String userName = requestBody.get("userName");
            String password = requestBody.get("password");
            String userPhone = requestBody.get("userPhone");
            String userEmail = requestBody.get("userEmail");
            String userBirthStr = requestBody.get("userBirth");

            // ProviderType 검증
            ProviderType providerType;
            try {
                providerType = ProviderType.valueOf(provider.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("유효하지 않은 provider 값입니다: " + provider);
            }

            // 날짜 파싱
            LocalDate userBirth = LocalDate.parse(userBirthStr);

            // 이메일 중복 체크
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

            // JWT 생성
            String token = jwtTokenUtil.generateToken(userEmail);

            return ResponseEntity.ok(Map.of("token", token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "입력 데이터 오류: " + e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "논리적 오류: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "회원가입 실패: " + e.getMessage()));
        }
    }
}
