package com.itdat.back.controller.auth;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class OAuthController {

    @PostMapping("/callback")
    public ResponseEntity<?> handleCallback(@RequestBody Map<String, String> request) {
        String code = request.get("code");

        // 인증 코드를 사용하여 액세스 토큰 요청
        String accessToken = fetchAccessToken(code);

        return ResponseEntity.ok(Collections.singletonMap("accessToken", accessToken));
    }

    private String fetchAccessToken(String code) {
        String url = "https://apis.worksmobile.com/r/{Domain ID}/oauth2/v2.0/token";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "RFqdC7PF7PVsvVnoN1Yy");
        params.add("client_secret", "uOtFkIpZS6");
        params.add("code", code);
        params.add("redirect_uri", "http://localhost:3000/callback");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> body = response.getBody();
            return body.get("access_token").toString();
        } else {
            throw new RuntimeException("Failed to fetch access token");
        }
    }
}
