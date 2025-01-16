package com.itdat.back.service.auth;

import com.itdat.back.entity.auth.NaverWorksConfig;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class NaverWorksAuthService {

    private final NaverWorksConfig naverWorksConfig;

    private static final String TOKEN_URL = System.getenv("TOKEN_URL");
    private static final String CLIENT_ID = System.getenv("CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("CLIENT_SECRET");
    private static final String REDIRECT_URI = System.getenv("REDIRECT_URI");

    public String fetchAccessToken(String authorizationCode) {
        RestTemplate restTemplate = new RestTemplate();

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 바디 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", CLIENT_ID);
        params.add("client_secret", CLIENT_SECRET);
        params.add("code", authorizationCode);
        params.add("redirect_uri", REDIRECT_URI);

        // 요청 객체 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // API 호출
        ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URL, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("access_token")) {
                return body.get("access_token").toString(); // 액세스 토큰 반환
            } else {
                throw new RuntimeException("Invalid response: access_token not found");
            }
        } else {
            throw new RuntimeException("Failed to fetch access token: " + response.getStatusCode());
        }
    }


    public NaverWorksAuthService(NaverWorksConfig naverWorksConfig) {
        this.naverWorksConfig = naverWorksConfig;
    }

    public String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        String url = naverWorksConfig.getBaseUrl() + "/oauth2/v2.0/token";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        String body = "grant_type=client_credentials"
                + "&client_id=" + naverWorksConfig.getClientId()
                + "&client_secret=" + naverWorksConfig.getClientSecret()
                + "&scope=mail.create";

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        Map<String, Object> responseBody = response.getBody();
        return responseBody.get("access_token").toString();
    }


}

