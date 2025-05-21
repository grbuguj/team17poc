package com.team17.poc.service;

import com.team17.poc.dto.OcrResultDto;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.regex.*;
import java.util.*;


// 일부 수정함 (구조 번경)
@Service
public class TesseractOcrService implements OcrService {

    private final ClovaOcrClient clovaOcrClient;

    public TesseractOcrService(ClovaOcrClient clovaOcrClient) {
        this.clovaOcrClient = clovaOcrClient;
    }

    @Override
    public OcrResultDto extractText(File imageFile) {
        String rawText;
        try {
            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
            tesseract.setLanguage("eng+kor");
            tesseract.setTessVariable("tessedit_char_whitelist", "0123456789:.년월일시분까지");
            tesseract.setPageSegMode(6);

            rawText = tesseract.doOCR(imageFile).replaceAll("[\\n\\r]", " ").trim();
            String extracted = extractByPattern(rawText);
            if (extracted != null) {
                System.out.println("🟢 Tesseract Ocr 인식 성공");
                return new OcrResultDto(extracted, true, rawText, "Tesseract");
            }
        } catch (TesseractException e) {
            rawText = "Tesseract OCR 실패: " + e.getMessage();
        }

        System.out.println("🟡 Clova OCR fallback 시작");
        // fallback to Clova
        String clovaRaw = clovaOcrClient.callClovaOcr(imageFile);

        System.out.println("🟢 Clova OCR 응답 수신: " + clovaRaw);


        if (clovaRaw != null && !clovaRaw.isBlank()) {
            String extracted = extractByPattern(clovaRaw);
            if (extracted != null) {
                return new OcrResultDto(extracted, true, clovaRaw, "Clova OCR (fallback)");
            } else {
                return new OcrResultDto(null, false, clovaRaw, "Clova OCR (fallback)");
            }
        }

        return new OcrResultDto(null, false, rawText, "Tesseract + Clova OCR 실패");
    }

    private String extractByPattern(String text) {
        List<String> patterns = Arrays.asList(
                "\\b\\d{4}[-./]\\d{2}[-./]\\d{2}\\b",                  // 2024-05-20
                "\\b\\d{2}[-./]\\d{2}[-./]\\d{2}\\b",                  // 24.05.20
                "\\b\\d{2}[-./]\\d{2}\\b",                             // 05.20
                "(?i)(EXP|유통기한)([:\\s]?)(\\d{4}[.-]\\d{2}[.-]\\d{2})" // 유통기한: 2024.05.20
        );

        for (String p : patterns) {
            Matcher matcher = Pattern.compile(p).matcher(text);
            if (matcher.find()) {
                String match = matcher.group();

                // 연도 없는 MM.DD 형식 보정
                if (match.matches("\\d{2}[./-]\\d{2}")) {
                    return "2025." + match.replaceAll("[-]", "."); // 예: 04.21 → 2025.04.21
                }

                // 24.05.20 같은 경우도 2025년으로 간주하고 보정 가능 (선택 사항)
                if (match.matches("\\d{2}[./-]\\d{2}[./-]\\d{2}")) {
                    // "24" 등 두 자리 연도는 2025 기준으로 보정할지 여부 선택 가능
                    // 우선은 그대로 출력
                    return match.replaceAll("[-]", ".");
                }

                return match.replaceAll("[-]", ".");
            }
        }
        return null;
    }

    @Override
    public OcrResultDto extractDate(File imageFile) {
        return extractText(imageFile);
    }
}

