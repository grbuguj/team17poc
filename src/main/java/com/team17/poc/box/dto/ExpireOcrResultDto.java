package com.team17.poc.box.dto;
// 유통기한 촬영 결과를 담아서, client로 응답하는 dto file.

import java.time.LocalDate;


public class ExpireOcrResultDto {
    private final String productName;
    private final String imageUrl;
    private final LocalDate captureDate;
    private final String expireDate;

    public ExpireOcrResultDto(String productName, String imageUrl, LocalDate capturedDate, String expireDate) {
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.captureDate = capturedDate;
        this.expireDate = expireDate;
    }

    public String getProductName() {
        return productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDate getCapturedDate() {
        return captureDate;
    }

    public String getExpireDate() {
        return expireDate;
    }
}
