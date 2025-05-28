package com.team17.poc.box.dto;
// 새롭게 추가한 dto
// 바코드 촬영 결과 임시 저장, 유통기한 촬영 후 합쳐서 출력하기 위한 file

import java.time.LocalDate;

public class TempScanResult {

    private final String imageUrl;
    private final String barcodeId;
    private final String productName;
    private final LocalDate captureDate;

    public TempScanResult(String imageUrl, String barcodeId, String productName, LocalDate captureDate) {
        this.imageUrl = imageUrl;
        this.barcodeId = barcodeId;
        this.productName = productName;
        this.captureDate = captureDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBarcodeId() {
        return barcodeId;
    }

    public String getProductName() {
        return productName;
    }

    public LocalDate getCaptureDate() {
        return captureDate;
    }
}
