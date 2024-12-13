package com.itdat.back.service.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class NaverWorksAuthService {
    @Value("${naver.works.client-id}")
    private String clientId;

    @Value("${naver.works.client-secret}")
    private String clientSecret;

    @Value("${naver.works.domain-id}")
    private String domainId;

    @Value("${naver.works.base-url}")
    private String baseUrl;

    public String getAccessToken() {
        String url = baseUrl + "/oauth2/v2.0/token";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("scope", "mail.send");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            return responseBody.get("access_token").toString();
        } else {
            throw new RuntimeException("Failed to get access token");
        }
    }
}
