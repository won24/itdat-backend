package com.itdat.back.controller.auth;

import com.itdat.back.entity.auth.User;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.service.auth.SocialOAuthService;
import com.itdat.back.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/oauth")
public class SocialAuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SocialOAuthService socialOAuthService;

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
    public ResponseEntity<?> socialLogin(@PathVariable String provider, @RequestHeader("Authorization") String accessToken) {
        System.out.println("socialLogin 호출됨");
        System.out.println("Provider: " + provider);
        System.out.println("AccessToken: " + accessToken);

        try {
            // Bearer 제거
            String token = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;

            // 소셜 제공자별 사용자 정보 가져오기
            Map<String, Object> userInfo;
            if (provider.equalsIgnoreCase("google")) {
                userInfo = socialOAuthService.verifyGoogleIdToken(token); // Google ID Token 검증
            } else {
                userInfo = socialOAuthService.getUserInfoFromOAuth(provider, token); // Kakao/Naver
            }

            String email;
            String providerId;

            // 소셜 제공자별로 응답 데이터 처리
            switch (provider.toLowerCase()) {
                case "google":
                    email = userInfo.get("email").toString();
                    providerId = userInfo.get("sub").toString();
                    break;
                case "kakao":
                    Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
                    email = kakaoAccount.get("email").toString();
                    providerId = userInfo.get("id").toString();
                    break;
                case "naver":
                    Map<String, Object> naverResponse = (Map<String, Object>) userInfo.get("response");
                    email = naverResponse.get("email").toString();
                    providerId = naverResponse.get("id").toString();
                    break;
                default:
                    throw new IllegalArgumentException("지원되지 않는 소셜 제공자입니다: " + provider);
            }

            // 기존 사용자 확인
            User existingUser = userRepository.findByUserEmail(email);

            if (existingUser != null) {
                // 이미 등록된 사용자
                String jwtToken = jwtTokenUtil.generateToken(existingUser.getUserEmail());
                return ResponseEntity.ok(Map.of("token", jwtToken, "requiresRegistration", false));
            } else {
                // 추가 정보 필요
                return ResponseEntity.ok(Map.of(
                        "providerId", providerId,
                        "email", email,
                        "requiresRegistration", true
                ));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "잘못된 요청: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "소셜 로그인 실패: " + e.getMessage()));
        }
    }




}
