package com.team17.poc.ocr.service;

import java.io.File;
import com.team17.poc.ocr.dto.OcrResultDto;

public interface OcrService {
    OcrResultDto extractText(File imageFile);

    OcrResultDto extractDate(File imageFile);
}
