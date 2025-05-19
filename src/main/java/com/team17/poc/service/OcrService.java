package com.team17.poc.service;

import java.io.File;
import com.team17.poc.dto.OcrResultDto;

public interface OcrService {
    OcrResultDto extractText(File imageFile);
}
