package com.team17.poc.service;

import com.team17.poc.dto.OcrResultDto;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.regex.*;
import java.util.*;

@Service
public class TesseractOcrService implements OcrService {

    @Override
    public OcrResultDto extractText(File imageFile) {
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        tesseract.setLanguage("eng+kor");
        tesseract.setTessVariable("tessedit_char_whitelist", "0123456789:.년월일시분까지");
        tesseract.setPageSegMode(6);

        try {
            String rawText = tesseract.doOCR(imageFile);
            rawText = rawText.replaceAll("[\\n\\r]", " ").trim();

            List<String> patterns = Arrays.asList(
                    "\\b\\d{4}[-./]\\d{2}[-./]\\d{2}\\b",
                    "\\b\\d{2}[-./]\\d{2}[-./]\\d{2}\\b",
                    "\\b\\d{2}[-./]\\d{2}\\b",
                    "(?i)(EXP|유통기한)([:\\s]?)(\\d{4}[.-]\\d{2}[.-]\\d{2})"
            );

            for (String p : patterns) {
                Matcher matcher = Pattern.compile(p).matcher(rawText);
                if (matcher.find()) {
                    return new OcrResultDto(matcher.group(), true, rawText, "Tesseract");
                }
            }

            return new OcrResultDto(null, false, rawText, "Tesseract");

        } catch (TesseractException e) {
            return new OcrResultDto(null, false, "OCR 실패: " + e.getMessage(), "Tesseract");
        }
    }
}
