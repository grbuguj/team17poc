package com.team17.poc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ClovaOcrClient {

    @Value("${clova.ocr.secret}")
    private String secret;

    @Value("${clova.ocr.invoke-url}")
    private String invokeUrl;

    public String callClovaOcr(File imageFile) {
        try {
            // === 1. payload 구성 ===
            String ext = getExtension(imageFile.getName());
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("version", "V1");
            messageMap.put("requestId", UUID.randomUUID().toString());
            messageMap.put("timestamp", System.currentTimeMillis());

            Map<String, String> imageMap = new HashMap<>();
            imageMap.put("format", ext);
            imageMap.put("name", "demo");
            messageMap.put("images", List.of(imageMap));

            ObjectMapper mapper = new ObjectMapper();
            String jsonMessage = mapper.writeValueAsString(messageMap);

            // === 2. multipart 요청 구성 ===
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-OCR-SECRET", secret);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("message", jsonMessage);
            body.add("file", new FileSystemResource(imageFile));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // === 3. 요청 전송 ===
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(invokeUrl, requestEntity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                return null;
            }

            // === 4. 텍스트 추출 ===
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode fields = root.path("images").get(0).path("fields");

            List<String> inferTexts = new ArrayList<>();
            for (JsonNode field : fields) {
                inferTexts.add(field.path("inferText").asText());
            }

            return extractDateFromText(inferTexts);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractDateFromText(List<String> texts) {
        List<String> numbers = new ArrayList<>();
        for (String text : texts) {
            Matcher matcher = Pattern.compile("\\d{1,4}").matcher(text);
            while (matcher.find()) {
                numbers.add(matcher.group());
            }
        }

        // YYYY.MM.DD 우선
        for (int i = 0; i < numbers.size() - 2; i++) {
            String y = numbers.get(i), m = numbers.get(i + 1), d = numbers.get(i + 2);
            if (y.length() == 4 && validMonthDay(m, d)) {
                return String.format("%s.%02d.%02d", y, Integer.parseInt(m), Integer.parseInt(d));
            }
        }

        // MM.DD → 2025.MM.DD 보정
        for (int i = 0; i < numbers.size() - 1; i++) {
            String m = numbers.get(i), d = numbers.get(i + 1);
            if (validMonthDay(m, d)) {
                return String.format("2025.%02d.%02d", Integer.parseInt(m), Integer.parseInt(d));
            }
        }

        return null;
    }

    private boolean validMonthDay(String m, String d) {
        try {
            int mm = Integer.parseInt(m);
            int dd = Integer.parseInt(d);
            return mm >= 1 && mm <= 12 && dd >= 1 && dd <= 31;
        } catch (Exception e) {
            return false;
        }
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf(".");
        return (dot != -1) ? filename.substring(dot + 1).toLowerCase() : "jpg";
    }
}
