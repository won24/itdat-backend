package com.itdat.back.service.auth;

import com.itdat.back.entity.auth.NaverWorksConfig;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class NaverWorksEmailService {

    private final NaverWorksAuthService authService; // 액세스 토큰 발급 서비스
    private final NaverWorksConfig naverWorksConfig; // 설정값 로드

    public NaverWorksEmailService(NaverWorksAuthService authService, NaverWorksConfig naverWorksConfig) {
        this.authService = authService;
        this.naverWorksConfig = naverWorksConfig;
    }

    public void sendEmail(String recipient, String subject, String content) {
        // 1. Access Token 발급
        String accessToken = authService.getAccessToken();

        // 2. API 엔드포인트 URL 구성
        String url = naverWorksConfig.getBaseUrl() + "/r/" + naverWorksConfig.getDomainId() + "/mail/v2/create";

        // 3. 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 4. 요청 바디 구성
        Map<String, Object> body = new HashMap<>();
        body.put("to", new String[]{recipient});
        body.put("subject", subject);
        Map<String, String> bodyContent = new HashMap<>();
        bodyContent.put("contentType", "text/html");
        bodyContent.put("content", content);
        body.put("body", bodyContent);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // 5. API 호출
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        // 6. 응답 확인
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("이메일 발송 성공: " + response.getBody());
        } else {
            System.err.println("이메일 발송 실패: " + response.getBody());
            throw new RuntimeException("Failed to send email, HTTP status: " + response.getStatusCode());
        }
    }
}
