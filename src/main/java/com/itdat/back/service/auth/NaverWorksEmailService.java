package com.itdat.back.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class NaverWorksEmailService {
    @Value("${naver.works.base-url}")
    private String baseUrl;

    @Value("${naver.works.domain-id}")
    private String domainId;

    @Autowired
    private NaverWorksAuthService authService;

    public void sendEmail(String to, String subject, String body) {
        String accessToken = authService.getAccessToken();
        String url = baseUrl + "/mail/v1/" + domainId + "/messages";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> emailContent = new HashMap<>();
        emailContent.put("from", "noreply@www.itdat.store");
        emailContent.put("to", Collections.singletonList(to));
        emailContent.put("subject", subject);
        emailContent.put("content", body);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(emailContent, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Email sent successfully");
        } else {
            System.err.println("Failed to send email: " + response.getBody());
        }
    }
}
