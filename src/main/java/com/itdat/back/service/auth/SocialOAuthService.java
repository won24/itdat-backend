package com.itdat.back.service.auth;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SocialOAuthService {

    private final RestTemplate restTemplate;

    public SocialOAuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 소셜 로그인에서 사용자 ID 가져오기
     *
     * @param provider 소셜 로그인 제공자(GOOGLE, NAVER, KAKAO)
     * @param accessToken 소셜 로그인 Access Token
     * @return 사용자 고유 ID
     */
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

    /**
     * 소셜 로그인에서 이메일 가져오기
     *
     * @param provider 소셜 로그인 제공자(GOOGLE, NAVER, KAKAO)
     * @param accessToken 소셜 로그인 Access Token
     * @return 사용자 이메일
     */
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

    // Google 사용자 정보 API 호출
    private Map<String, Object> fetchGoogleUserInfo(String accessToken) {
        String googleApiUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
        return sendGetRequest(googleApiUrl, accessToken);
    }

    // Naver 사용자 정보 API 호출
    private Map<String, Object> fetchNaverUserInfo(String accessToken) {
        String naverApiUrl = "https://openapi.naver.com/v1/nid/me";
        return sendGetRequest(naverApiUrl, accessToken);
    }

    // Kakao 사용자 정보 API 호출
    private Map<String, Object> fetchKakaoUserInfo(String accessToken) {
        String kakaoApiUrl = "https://kapi.kakao.com/v2/user/me";
        return sendGetRequest(kakaoApiUrl, accessToken);
    }

    // HTTP GET 요청 공통 메서드
    private Map<String, Object> sendGetRequest(String url, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        return response.getBody();
    }
}

