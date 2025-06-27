package com.team17.poc.box.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class LocationRequestDto {
    private String name;
    private String description;
    private MultipartFile image;
}
