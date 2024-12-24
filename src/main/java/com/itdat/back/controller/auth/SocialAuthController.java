package com.itdat.back.controller.auth;

import com.itdat.back.entity.auth.ProviderType;
import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.auth.UserType;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.service.auth.SocialOAuthService;
import com.itdat.back.utils.JwtTokenUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
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
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(
            @RequestBody(required = false) Map<String, String> request,
            @RequestHeader(value = "Authorization", required = false) String accessToken
    ) {
        try {
            String token = null;
            Map<String, Object> userInfo;

            // Google 처리
            if (request == null || !request.containsKey("idToken")) {
                return ResponseEntity.badRequest().body("Missing ID Token");
            }
            token = request.get("idToken");
            userInfo = socialOAuthService.verifyGoogleIdToken(token);

            // 사용자 정보 처리
            String email = userInfo.get("email").toString();
            String providerId = userInfo.get("sub").toString();

            User existingUser = userRepository.findByUserEmail(email);

            if (existingUser != null) {
                String jwtToken = jwtTokenUtil.generateToken(existingUser.getUserEmail());
                return ResponseEntity.ok(Map.of("token", jwtToken, "requiresRegistration", false));
            } else {
                return ResponseEntity.ok(Map.of(
                        "providerId", providerId,
                        "email", email,
                        "requiresRegistration", true
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Google 로그인 실패: " + e.getMessage()));
        }
    }


    @PostMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(
            @RequestHeader(value = "Authorization", required = true) String accessToken
    ) {
//        System.out.println("kakaoLogin 호출됨");

        try {
            // Access Token 유효성 검사 및 사용자 정보 가져오기
            if (accessToken == null || !accessToken.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Missing Access Token");
            }

            String token = accessToken.substring(7);
            Map<String, Object> userInfo = socialOAuthService.getUserInfoFromOAuth("kakao", token);

//            System.out.println("UserInfo: " + userInfo);

            // Kakao 응답에서 이메일과 고유 ID 가져오기
            String email = socialOAuthService.getKakaoEmail(userInfo);
            String providerId = socialOAuthService.getKakaoProviderId(userInfo);

//            System.out.println("Email: " + email);
//            System.out.println("Provider ID: " + providerId);

            // DB에서 사용자 정보 조회
            User existingUser = userRepository.findByUserEmail(email);
//            System.out.println("Existing User: " + existingUser);

            // 사용자 존재 여부에 따른 처리
            if (existingUser != null) {
                String jwtToken = jwtTokenUtil.generateToken(existingUser.getUserEmail());
                return ResponseEntity.ok(Map.of("token", jwtToken, "requiresRegistration", false));
            } else {
                return ResponseEntity.ok(Map.of(
                        "providerId", providerId,
                        "email", email,
                        "requiresRegistration", true
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "카카오 로그인 실패: " + e.getMessage()));
        }
    }

    @PostMapping("/naver")
    public ResponseEntity<?> naverLogin(
            @RequestHeader(value = "Authorization", required = true) String accessToken
    ) {
        System.out.println("naverLogin 호출됨");

        try {
            // Access Token 유효성 검사 및 사용자 정보 가져오기
            if (accessToken == null || !accessToken.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Missing Access Token");
            }

            String token = accessToken.substring(7);
            Map<String, Object> userInfo = socialOAuthService.getUserInfoFromOAuth("naver", token);

            System.out.println("UserInfo: " + userInfo);

            // Naver 응답에서 이메일과 고유 ID 가져오기
            Map<String, Object> response = (Map<String, Object>) userInfo.get("response");
            String email = (String) response.get("email");
            String providerId = (String) response.get("id");

            if (email == null) {
                throw new IllegalArgumentException("이메일 정보가 제공되지 않았습니다.");
            }

            System.out.println("Email: " + email);
            System.out.println("Provider ID: " + providerId);

            // DB에서 사용자 정보 조회
            User existingUser = userRepository.findByUserEmail(email);
            System.out.println("Existing User: " + existingUser);

            // 사용자 존재 여부에 따른 처리
            if (existingUser != null) {
                String jwtToken = jwtTokenUtil.generateToken(existingUser.getUserEmail());
                return ResponseEntity.ok(Map.of("token", jwtToken, "requiresRegistration", false));
            } else {
                return ResponseEntity.ok(Map.of(
                        "providerId", providerId,
                        "email", email,
                        "requiresRegistration", true
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "네이버 로그인 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/callback/naver")
    public void handleNaverCallback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            HttpServletResponse response
    ) throws IOException {
        if (code == null || state == null) {
            response.sendRedirect("http://localhost:3000/login?error=missing_code_or_state");
            return;
        }

        try {
            // Access Token 요청
            RestTemplate restTemplate = new RestTemplate();
            String tokenUrl = "https://nid.naver.com/oauth2.0/token";

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(tokenUrl)
                    .queryParam("grant_type", "authorization_code")
                    .queryParam("client_id", "Kk0mlnghLzPAi0TpquZj")
                    .queryParam("client_secret", "mwNpwGjHrR")
                    .queryParam("code", code)
                    .queryParam("state", state);

            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    null,
                    Map.class
            );

            Map<String, Object> tokenResponse = responseEntity.getBody();
            String accessToken = (String) tokenResponse.get("access_token");

            // 사용자 정보 가져오기
            Map<String, Object> userInfo = socialOAuthService.getNaverUserInfo(accessToken);
            Map<String, Object> responseDetails = (Map<String, Object>) userInfo.get("response");

            String email = (String) responseDetails.get("email");
            String naverId = (String) responseDetails.get("id");

            if (email == null || naverId == null) {
                throw new IllegalArgumentException("이메일 또는 Provider ID가 없습니다.");
            }

            // 기존 사용자 확인
            User existingUser = userRepository.findByUserEmail(email);

            if (existingUser != null) {
                // 로그인 성공: JWT 생성 후 메인 페이지로 리다이렉트
                String jwtToken = jwtTokenUtil.generateToken(existingUser.getUserEmail());
                response.sendRedirect("http://localhost:3000?token=" + jwtToken);
            } else {
                // 회원가입이 필요한 사용자: Register 페이지로 리다이렉트
                String redirectUrl = String.format(
                        "http://localhost:3000/register?providerId=%s&email=%s&providerType=NAVER",
                        URLEncoder.encode(naverId, "UTF-8"),
                        URLEncoder.encode(email, "UTF-8")
                );
                response.sendRedirect(redirectUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("http://localhost:3000/login?error=naver_login_failed");
        }
    }



    @PostMapping("/social/register")
    public ResponseEntity<?> registerSocialUser(@RequestBody Map<String, String> requestBody) {
        try {
            // 요청 데이터 로깅
            System.out.println("Request Body: " + requestBody);
            System.out.println("ProviderType: " + requestBody.get("providerType"));

            // 요청 데이터 추출
            String userId = requestBody.get("userId");
            String userName = requestBody.get("userName");
            String password = requestBody.get("password");
            String confirmPassword = requestBody.get("confirmPassword");
            String userPhone = requestBody.get("userPhone");
            String userEmail = requestBody.get("userEmail");
            String userBirthStr = requestBody.get("userBirth");
            String userTypeStr = requestBody.get("userType");
            String company = requestBody.get("company");
            String companyRank = requestBody.get("companyRank");
            String companyDept = requestBody.get("companyDept");
            String companyFax = requestBody.get("companyFax");
            String companyAddr = requestBody.get("companyAddr");
            String companyAddrDetail = requestBody.get("companyAddrDetail");
            String companyPhone = requestBody.get("companyPhone");
            String providerTypeStr = requestBody.get("providerType");

            // 필수 필드 검증
            if (userName == null || userName.isEmpty()) {
                throw new IllegalArgumentException("이름은 필수 입력 항목입니다.");
            }
            if (password == null || password.isEmpty()) {
                throw new IllegalArgumentException("비밀번호는 필수 입력 항목입니다.");
            }
            if (!password.equals(confirmPassword)) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
            if (userEmail == null || userEmail.isEmpty()) {
                throw new IllegalArgumentException("이메일은 필수 입력 항목입니다.");
            }
            if (userBirthStr == null || userBirthStr.isEmpty()) {
                throw new IllegalArgumentException("생년월일은 필수 입력 항목입니다.");
            }
            if (userTypeStr == null || userTypeStr.isEmpty()) {
                throw new IllegalArgumentException("유저 타입은 필수 입력 항목입니다.");
            }
            if (providerTypeStr == null || providerTypeStr.isEmpty()) {
                throw new IllegalArgumentException("소셜이 확인되지 않습니다.");
            }


            // 중복 이메일 체크
            if (userRepository.findByUserEmail(userEmail) != null) {
                throw new IllegalStateException("이미 가입된 이메일입니다.");
            }

            // 데이터 변환
            LocalDate userBirth = LocalDate.parse(userBirthStr);
            UserType userType = UserType.valueOf(userTypeStr.toUpperCase());
            ProviderType providerType = ProviderType.valueOf(providerTypeStr.toUpperCase());

            // 신규 사용자 생성
            User newUser = new User();
            newUser.setUserId(userId);
            newUser.setUserName(userName);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setUserPhone(userPhone);
            newUser.setUserEmail(userEmail);
            newUser.setUserBirth(userBirth);
            newUser.setUserType(userType);
            newUser.setCompany(company);
            newUser.setCompanyRank(companyRank);
            newUser.setCompanyDept(companyDept);
            newUser.setCompanyFax(companyFax);
            newUser.setCompanyAddr(companyAddr);
            newUser.setCompanyAddrDetail(companyAddrDetail);
            newUser.setCompanyPhone(companyPhone);
            newUser.setProviderType(providerType);

            // 사용자 저장
            userRepository.save(newUser);

            // JWT 토큰 생성
            String token = jwtTokenUtil.generateToken(userEmail);

            // 응답 반환
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "회원가입 실패: " + e.getMessage()));
        }
    }

}
