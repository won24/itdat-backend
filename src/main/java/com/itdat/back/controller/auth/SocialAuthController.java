package com.itdat.back.controller.auth;

import com.itdat.back.entity.auth.ProviderType;
import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.auth.UserType;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.service.auth.SocialOAuthService;
import com.itdat.back.utils.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
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
@CrossOrigin
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

    /**
     * Google 소셜 로그인 처리
     *
     * @param request       요청 바디(Map 형식): Google ID 토큰을 포함
     * @param accessToken   요청 헤더에 포함된 Authorization 토큰 (선택적)
     * @return ResponseEntity: 로그인 결과
     *         - 성공: JWT 토큰 또는 추가 회원가입 여부
     *         - 실패: HTTP 오류 코드 및 메시지
     * @throws HttpStatus.BAD_REQUEST          잘못된 입력 데이터
     * @throws HttpStatus.INTERNAL_SERVER_ERROR Google 로그인 실패
     */
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(
            @RequestBody(required = false) Map<String, String> request,
            @RequestHeader(value = "Authorization", required = false) String accessToken
    ) {
        try {
            String token = null;
            Map<String, String> userInfo;

            // Google 처리
            if (request == null || !request.containsKey("idToken")) {
                return ResponseEntity.badRequest().body("Missing ID Token");
            }
            token = request.get("idToken");
            userInfo = socialOAuthService.verifyGoogleIdToken(token);

            // 사용자 정보 처리
            String email = userInfo.get("email").toString();
            String providerId = userInfo.get("sub").toString();

            // 사용자 조회
            User existingUser = userRepository.findByUserEmail(email);

            if (existingUser != null) {
                // 기존 사용자: 로그인 처리
                String jwtToken = jwtTokenUtil.generateToken(existingUser.getUserEmail());
                return ResponseEntity.ok(Map.of("token", jwtToken, "requiresRegistration", false));
            } else {
                // 새로운 사용자: 회원가입 필요
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


    /**
     * Kakao 소셜 로그인 처리
     *
     * @param accessToken   요청 헤더에 포함된 Kakao Access Token
     * @return ResponseEntity: 로그인 결과
     *         - 성공: JWT 토큰 또는 추가 회원가입 여부
     *         - 실패: HTTP 오류 코드 및 메시지
     * @throws HttpStatus.BAD_REQUEST          Access Token 누락
     * @throws HttpStatus.INTERNAL_SERVER_ERROR Kakao 로그인 실패
     */
    @PostMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(
            @RequestHeader(value = "Authorization", required = true) String accessToken
    ) {

        try {
            // Access Token 유효성 검사 및 사용자 정보 가져오기
            if (accessToken == null || !accessToken.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Missing Access Token");
            }

            String token = accessToken.substring(7);
            Map<String, Object> userInfo = socialOAuthService.getUserInfoFromOAuth("kakao", token);

            // Kakao 응답에서 이메일과 고유 ID 가져오기
            String email = socialOAuthService.getKakaoEmail(userInfo);
            String providerId = socialOAuthService.getKakaoProviderId(userInfo);

            // DB에서 사용자 정보 조회
            User existingUser = userRepository.findByUserEmail(email);

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

    /**
     * Kakao OAuth Callback 처리
     *
     * @param requestBody 요청 바디(Map 형식): Kakao 인증 코드
     * @return ResponseEntity: 로그인 결과
     *         - 성공: JWT 토큰 또는 추가 회원가입 여부
     *         - 실패: HTTP 오류 코드 및 메시지
     * @throws HttpStatus.BAD_REQUEST          Authorization Code 누락
     * @throws HttpStatus.INTERNAL_SERVER_ERROR Kakao OAuth 처리 실패
     */
    @PostMapping("/callback/kakao")
    public ResponseEntity<?> handleKakaoCallback(@RequestBody Map<String, String> requestBody) {
        String code = requestBody.get("code");
        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body("Authorization Code is missing");
        }

        try {
            String accessToken = socialOAuthService.getAccessTokenFromKakao(code);
            Map<String, Object> userInfo = socialOAuthService.getUserInfoFromOAuth("kakao", accessToken);

            String email = socialOAuthService.getKakaoEmail(userInfo);
            String providerId = socialOAuthService.getKakaoProviderId(userInfo);

            if (email == null || providerId == null) {
                throw new IllegalArgumentException("Email or Provider ID is missing");
            }

            User existingUser = userRepository.findByUserEmail(email);

            if (existingUser != null) {
                String jwtToken = jwtTokenUtil.generateToken(existingUser.getUserEmail());
                return ResponseEntity.ok(Map.of("requiresRegistration", false, "token", jwtToken));
            } else {
                return ResponseEntity.ok(Map.of("requiresRegistration", true, "email", email, "providerId", providerId));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during Kakao login: " + e.getMessage());
        }
    }


    /**
     * Naver 소셜 로그인 처리
     *
     * @param accessToken   요청 헤더에 포함된 Naver Access Token
     * @return ResponseEntity: 로그인 결과
     *         - 성공: JWT 토큰 또는 추가 회원가입 여부
     *         - 실패: HTTP 오류 코드 및 메시지
     * @throws HttpStatus.BAD_REQUEST          Access Token 누락
     * @throws HttpStatus.INTERNAL_SERVER_ERROR Naver 로그인 실패
     */
    @PostMapping("/naver")
    public ResponseEntity<?> naverLogin(
            @RequestHeader(value = "Authorization", required = true) String accessToken
    ) {
        try {
            // Access Token 유효성 검사 및 사용자 정보 가져오기
            if (accessToken == null || !accessToken.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Missing Access Token");
            }

            String token = accessToken.substring(7);
            Map<String, Object> userInfo = socialOAuthService.getUserInfoFromOAuth("naver", token);

            // Naver 응답에서 이메일과 고유 ID 가져오기
            Map<String, Object> response = (Map<String, Object>) userInfo.get("response");
            String email = (String) response.get("email");
            String providerId = (String) response.get("id");

            if (email == null) {
                throw new IllegalArgumentException("이메일 정보가 제공되지 않았습니다.");
            }

            // DB에서 사용자 정보 조회
            User existingUser = userRepository.findByUserEmail(email);

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

    /**
     * Naver OAuth Callback 처리
     *
     * @param code          요청 파라미터: 인증 코드
     * @param state         요청 파라미터: 상태 토큰
     * @param request       HttpServletRequest 객체
     * @param response      HttpServletResponse 객체
     * @throws IOException  리다이렉트 처리 중 오류
     */
    @GetMapping("/callback/naver")
    public void handleNaverCallback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            HttpServletRequest request,
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

            if (accessToken == null) {
                throw new IllegalArgumentException("Access Token을 가져오지 못했습니다.");
            }

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
                // 로그인 성공: JWT 생성 후 리다이렉트 처리
                String jwtToken = jwtTokenUtil.generateToken(existingUser.getUserEmail());

                // User-Agent를 확인하여 모바일 또는 웹 환경 구분
                String userAgent = request.getHeader("User-Agent");
                if (userAgent != null && userAgent.toLowerCase().contains("mobile")) {
                    // 모바일 환경: /main 스크린으로 리다이렉트
                    String mobileRedirectUrl = String.format(
                            "myapp://main?token=%s",
                            URLEncoder.encode(jwtToken, "UTF-8")
                    );
                    response.sendRedirect(mobileRedirectUrl);
                } else {
                    // 웹 환경: 기존 URL로 리다이렉트
                    response.sendRedirect("http://localhost:3000?token=" + jwtToken);
                }
            } else {
                // 회원가입이 필요한 사용자: Register 페이지로 리다이렉트
                String redirectUrl = String.format(
                        "http://localhost:3000/register?providerId=%s&email=%s&providerType=NAVER",
                        URLEncoder.encode(naverId, "UTF-8"),
                        URLEncoder.encode(email, "UTF-8")
                );

                // 모바일 또는 웹 환경에 따라 리다이렉트 처리
                String userAgent = request.getHeader("User-Agent");
                if (userAgent != null && userAgent.toLowerCase().contains("mobile")) {
                    // 모바일 환경: Custom Scheme으로 리다이렉트
                    String mobileRedirectUrl = String.format(
                            "myapp://register?providerId=%s&email=%s&providerType=NAVER",
                            URLEncoder.encode(naverId, "UTF-8"),
                            URLEncoder.encode(email, "UTF-8")
                    );
                    response.sendRedirect(mobileRedirectUrl);
                } else {
                    // 웹 환경: 기존 URL로 리다이렉트
                    response.sendRedirect(redirectUrl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("http://localhost:3000/login?error=naver_login_failed");
        }
    }



    /**
     * 소셜 사용자 회원가입
     *
     * @param requestBody 요청 바디(Map 형식):
     *                     - userId: 소셜 로그인 고유 ID
     *                     - userName: 사용자 이름
     *                     - password: 사용자 비밀번호
     *                     - confirmPassword: 비밀번호 확인
     *                     - userPhone: 사용자 전화번호
     *                     - userEmail: 사용자 이메일
     *                     - userBirth: 사용자 생년월일(yyyy-MM-dd 형식)
     *                     - userType: 사용자 유형
     *                     - company: 회사명
     *                     - companyRank: 직급
     *                     - companyDept: 부서명
     *                     - companyFax: 회사 팩스번호
     *                     - companyAddr: 회사 주소
     *                     - companyAddrDetail: 회사 상세주소
     *                     - companyPhone: 회사 전화번호
     *                     - providerType: 소셜 제공자(GOOGLE, NAVER, KAKAO)
     * @return ResponseEntity: 회원가입 결과
     *         - 성공: JWT 토큰
     *         - 실패: HTTP 오류 코드 및 메시지
     * @throws HttpStatus.BAD_REQUEST          필수 필드 누락
     * @throws HttpStatus.CONFLICT             중복된 이메일
     * @throws HttpStatus.INTERNAL_SERVER_ERROR 회원가입 실패
     */
    @PostMapping("/social/register")
    public ResponseEntity<?> registerSocialUser(@RequestBody Map<String, String> requestBody) {
        try {
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
