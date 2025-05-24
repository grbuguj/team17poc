package com.team17.poc.ocr.controller;

import com.team17.poc.ocr.dto.OcrResultDto;
import com.team17.poc.ocr.service.OcrService;
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

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        File tempFile = File.createTempFile("ocr-", file.getOriginalFilename());
        file.transferTo(tempFile);

        OcrResultDto result = ocrService.extractDate(tempFile);
        tempFile.delete();

        model.addAttribute("ocrResult", result); // ✅ 전체 객체 넘기기
        return "index"; // index.html 렌더링
    }

}
