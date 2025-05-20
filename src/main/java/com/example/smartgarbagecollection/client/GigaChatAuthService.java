package com.example.smartgarbagecollection.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GigaChatAuthService {

    @Value("${gigachat.oauth-url}")
    private String oauthUrl;

    @Value("${gigachat.auth-key}")
    private String authKey;

    @Value("${gigachat.scope}")
    private String scope;

    private final RestTemplate restTemplate;

    private String accessToken;
    private Instant tokenExpiry;

    public String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(tokenExpiry)) {
            fetchNewToken();
        }
        return accessToken;
    }

    private void fetchNewToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + authKey);
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        headers.set("Accept", "application/json");
        headers.set("RqUID", java.util.UUID.randomUUID().toString());

        HttpEntity<String> request = new HttpEntity<>("scope=" + scope, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                oauthUrl, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> body = response.getBody();
            this.accessToken = (String) body.get("access_token");
            int expiresIn = (int) body.get("expires_in");
            this.tokenExpiry = Instant.now().plusSeconds(expiresIn - 60);
        } else {
            throw new RuntimeException("Не удалось получить токен GigaChat: " + response.getStatusCode());
        }
    }
}

