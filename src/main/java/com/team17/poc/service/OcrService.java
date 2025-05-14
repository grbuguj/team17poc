package com.team17.poc.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;
// 추가
import java.util.regex.*;
import java.util.*;

@Service
public class OcrService {

    public String extractText(File imageFile) {
        ITesseract tesseract = new Tesseract();

        // Tesseract 설치 경로 지정
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        tesseract.setLanguage("eng+kor");  // 영어 + 한국어 지원
        tesseract.setTessVariable("tessedit_char_whitelist", "0123456789:.년월일시분까지");
        // 추가
        tesseract.setPageSegMode(6); // PSM 6: 한 줄 or 한 문단으로 된 텍스트임을 알려주는 코드

        try {
            String rawText = tesseract.doOCR(imageFile);

            List<String> dataPatterns = Arrays.asList(
                    "\\b\\d{4}\\.\\d{2}\\.\\d{2}\\b",  // 2025.10.15
                    "\\b\\d{2}\\.\\d{2}\\.\\d{2}\\b",  // 25.10.15
                    "\\b\\d{2}\\.\\d{2}\\b"            // 10.15
            );

            // 위의 패턴에 대해 매칭을 시도함.
            for (String pattern : dataPatterns) {
                Pattern p = Pattern.compile(pattern);  // 정규표현식 컴아리
                Matcher m = p.matcher(rawText);   // ocr 결과와 매칭
                if(m.find()) {
                    return m.group();  // 가장 먼저 찾은 날짜 형식만 리턴.
                }
            }

            // 위의 형식에 맞는게 없으면 그냥 전체 반환.
            return "날짜 형식 없음: " + rawText;
            // return tesseract.doOCR(imageFile);

        } catch (TesseractException e) {
            e.printStackTrace();
            return "OCR 실패: " + e.getMessage();
        }
    }
}
