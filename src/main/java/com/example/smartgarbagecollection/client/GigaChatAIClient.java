package com.example.smartgarbagecollection.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Component
@RequiredArgsConstructor
public class GigaChatAIClient {

    private final GigaChatAuthService authService;

    @Value("${gigachat.api-url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public String testMessageOnly() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(authService.getAccessToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "GigaChat-Max");
            body.put("messages", List.of(
                    Map.of("role", "user", "content", "Привет, как дела?")
            ));

            System.out.println("Отправляем запрос в GigaChat: " + body); // ← Вот здесь

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }

            return "Ошибка при получении ответа";

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при отправке сообщения в GigaChat", e);
        }
    }

    public String analyzeImage(File imageFile) {
        try {
            //byte[] fileContent = FileUtils.readFileToByteArray(imageFile);
            //String base64Image = Base64.getEncoder().encodeToString(fileContent);

            String base64Image = "test-image-data";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(authService.getAccessToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "GigaChat-Max");
            body.put("messages", List.of(
                    Map.of("role", "user", "content", "Изображение:"),
                    Map.of("role", "user", "content", base64Image),
                    Map.of("role", "user", "content", "Мусорная площадка заполнена или пуста?")
            ));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            log.info("Отправляем запрос в GigaChat: " + body);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }

            return "Ошибка при анализе изображения";

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при отправке изображения в GigaChat", e);
        }
    }
}
