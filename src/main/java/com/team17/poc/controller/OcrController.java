package com.team17.poc.controller;

import com.team17.poc.dto.OcrResultDto;
import com.team17.poc.service.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;


import java.io.File;
import java.io.IOException;

@Controller // restcontroller -> controller 수정.
@RequestMapping("/ocr")
public class OcrController {

    private final OcrService ocrService;

    public OcrController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    /*
    @Autowired
    public OcrController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    @PostMapping("/upload")
    public ResponseEntity<OcrResultDto> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(new OcrResultDto(null, false, "파일이 비어 있음", "Tesseract"));
        }

        String originalFilename = file.getOriginalFilename() == null ? "unknown" : file.getOriginalFilename();
        File tempFile = File.createTempFile("ocr-", originalFilename);
        file.transferTo(tempFile);

        OcrResultDto result = ocrService.extractText(tempFile);
        tempFile.delete();

        return ResponseEntity.ok(result);
    }

     */

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        File tempFile = File.createTempFile("ocr-", file.getOriginalFilename());
        file.transferTo(tempFile);
        OcrResultDto result = ocrService.extractText(tempFile);
        tempFile.delete();

        model.addAttribute("ocrResult", result.getExtractedDate());
        return "index"; // index.html 렌더링
    }
}
