package com.example.smartgarbagecollection.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // Или WebClient
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GeminiRestApiClient {

    @Value("${google.gemini.api-key}")
    private String apiKey;

    @Value("${google.gemini.model-name}")
    private String modelName;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public String sendContentWithImage(String promptText, String imagePath) throws IOException {
        String apiUrl = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", modelName, apiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 1. Кодируем изображение в Base64
        String base64Image = ImageUtils.encodeImageToBase64(imagePath);
        String mimeType = ImageUtils.getMimeType(imagePath);

        // 2. Создаем тело запроса в формате JSON
        ObjectNode requestBody = objectMapper.createObjectNode();
        ArrayNode contents = requestBody.putArray("contents");
        ObjectNode content = contents.addObject();
        content.put("role", "user");
        ArrayNode parts = content.putArray("parts");

        // Добавляем текстовую часть
        parts.addObject().put("text", promptText);

        // Добавляем часть с изображением
        ObjectNode inlineData = parts.addObject().putObject("inlineData");
        inlineData.put("mimeType", mimeType);
        inlineData.put("data", base64Image);

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        try {
            String response = restTemplate.postForObject(apiUrl, request, String.class);
            JsonNode root = objectMapper.readTree(response);

            // Извлекаем текст из ответа (та же логика)
            JsonNode candidates = root.get("candidates");
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode contentNode = firstCandidate.get("content");
                if (contentNode != null) {
                    JsonNode partsNode = contentNode.get("parts");
                    if (partsNode != null && partsNode.isArray() && partsNode.size() > 0) {
                        StringBuilder textBuilder = new StringBuilder();
                        for (JsonNode part : partsNode) {
                            if (part.has("text")) {
                                textBuilder.append(part.get("text").asText());
                            }
                        }
                        if (textBuilder.length() > 0) {
                            return textBuilder.toString();
                        }
                    }
                }
            }
            return "Ошибка: Ответ получен, но не содержит ожидаемого текстового контента.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка: Не удалось получить ответ от Gemini. " + e.getMessage();
        }
    }
}