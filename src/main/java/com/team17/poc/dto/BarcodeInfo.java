package com.team17.poc.dto;

public class BarcodeInfo {
    private String name;
    private String image;

    public BarcodeInfo(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
