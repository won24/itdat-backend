package com.itdat.back.service.auth;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Map;

@Service
public class SocialOAuthService {

    private final RestTemplate restTemplate;

    public SocialOAuthService() {
        this.restTemplate = new RestTemplate();
    }

    // 사용자 ID 가져오기
    public String getProviderIdFromOAuth(String provider, String accessToken) {
        switch (provider.toLowerCase()) {
            case "google":
                return fetchGoogleUserInfo(accessToken).get("sub").toString();
            case "naver":
                return ((Map<String, String>) fetchNaverUserInfo(accessToken).get("response")).get("id");
            case "kakao":
                return fetchKakaoUserInfo(accessToken).get("id").toString();
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }

    // 이메일 가져오기
    public String getEmailFromOAuth(String provider, String accessToken) {
        switch (provider.toLowerCase()) {
            case "google":
                return fetchGoogleUserInfo(accessToken).get("email").toString();
            case "naver":
                return ((Map<String, String>) fetchNaverUserInfo(accessToken).get("response")).get("email");
            case "kakao":
                return ((Map<String, String>) fetchKakaoUserInfo(accessToken).get("kakao_account")).get("email").toString();
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }

    // Kakao 사용자 이메일 가져오기
    public String getKakaoEmail(Map<String, Object> userInfo) {
        if (userInfo.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
            if (kakaoAccount.containsKey("email")) {
                return kakaoAccount.get("email").toString();
            }
        }
        throw new IllegalArgumentException("Kakao email is missing or not provided.");
    }

    // Kakao 사용자 ID 가져오기
    public String getKakaoProviderId(Map<String, Object> userInfo) {
        if (userInfo.containsKey("id")) {
            return userInfo.get("id").toString();
        }
        throw new IllegalArgumentException("Kakao provider ID is missing.");
    }

    // Google 사용자 정보 가져오기
    private Map<String, Object> fetchGoogleUserInfo(String accessToken) {
        String googleApiUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
        return sendGetRequest(googleApiUrl, accessToken);
    }

    // Naver 사용자 정보 가져오기
    private Map<String, Object> fetchNaverUserInfo(String accessToken) {
        String naverApiUrl = "https://openapi.naver.com/v1/nid/me";
        return sendGetRequest(naverApiUrl, accessToken);
    }

    // Kakao 사용자 정보 가져오기
    private Map<String, Object> fetchKakaoUserInfo(String accessToken) {
        String kakaoApiUrl = "https://kapi.kakao.com/v2/user/me";
        return sendGetRequest(kakaoApiUrl, accessToken);
    }

    // HTTP GET 요청 공통 메서드
    private Map<String, Object> sendGetRequest(String url, String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken.trim());
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to fetch user info from Kakao: " + e.getMessage());
        }
    }

    // Google ID Token 검증
    public Map<String, Object> verifyGoogleIdToken(String idToken) {
        try {
            // Google 공개 키를 사용하여 ID Token 검증
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList("975498336283-bkjiua72fbhkdi0phtugk08sqqhaakff.apps.googleusercontent.com"))
                    .setIssuer("https://accounts.google.com")
                    .build();

            // ID Token 검증 및 파싱
            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken != null) {
                GoogleIdToken.Payload payload = googleIdToken.getPayload();

                // 사용자 정보 추출
                return Map.of(
                        "email", payload.getEmail(),
                        "email_verified", payload.getEmailVerified(),
                        "sub", payload.getSubject(),
                        "name", payload.get("name"),
                        "picture", payload.get("picture"),
                        "given_name", payload.get("given_name"),
                        "family_name", payload.get("family_name")
                );
            } else {
                throw new IllegalArgumentException("Invalid Google ID Token");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error verifying Google ID Token: " + e.getMessage(), e);
        }
    }


    // 다른 소셜 제공자에서 사용자 정보 가져오기
    public Map<String, Object> getUserInfoFromOAuth(String provider, String accessToken) {
        String apiUrl;

        switch (provider.toLowerCase()) {
            case "kakao":
                apiUrl = "https://kapi.kakao.com/v2/user/me";
                break;
            case "naver":
                apiUrl = "https://openapi.naver.com/v1/nid/me";
                break;
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken.trim());
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to fetch user info from " + provider + ": " + e.getMessage());
        }
    }

    public Map<String, Object> getNaverUserInfo(String accessToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String userInfoUrl = "https://openapi.naver.com/v1/nid/me";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new IllegalArgumentException("네이버 사용자 정보를 가져올 수 없습니다: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("네이버 사용자 정보 요청 실패: " + e.getMessage(), e);
        }
    }
    public String getAccessTokenFromKakao(String code) throws Exception {
        String redirectUri = "kakao387812a6ae2897c3e9e59952c211374e://oauth";
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "6a5e1f66918d24577f91727acffc819a");
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(params, null),
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !responseBody.containsKey("access_token")) {
                throw new Exception("카카오 Access Token 요청 실패: 응답 데이터가 없습니다.");
            }

            return (String) responseBody.get("access_token");
        } catch (HttpClientErrorException e) {
            throw new Exception("카카오 Access Token 요청 실패: " + e.getMessage());
        }
    }
}



