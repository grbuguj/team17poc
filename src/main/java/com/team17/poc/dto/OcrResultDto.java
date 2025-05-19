package com.team17.poc.dto;

public class OcrResultDto {
    private String extractedDate;
    private boolean success;
    private String rawText;
    private String engine;

    public OcrResultDto() {}

    public OcrResultDto(String extractedDate, boolean success, String rawText, String engine) {
        this.extractedDate = extractedDate;
        this.success = success;
        this.rawText = rawText;
        this.engine = engine;
    }

    public String getExtractedDate() { return extractedDate; }
    public boolean isSuccess() { return success; }
    public String getRawText() { return rawText; }
    public String getEngine() { return engine; }

    public void setExtractedDate(String extractedDate) { this.extractedDate = extractedDate; }
    public void setSuccess(boolean success) { this.success = success; }
    public void setRawText(String rawText) { this.rawText = rawText; }
    public void setEngine(String engine) { this.engine = engine; }
}
