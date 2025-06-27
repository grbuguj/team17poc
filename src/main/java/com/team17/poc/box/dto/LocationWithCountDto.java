package com.team17.poc.box.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationWithCountDto {
    private Long locationId;
    private String name;
    private String description;
    private String imagePath;
    private int itemCount;
}
