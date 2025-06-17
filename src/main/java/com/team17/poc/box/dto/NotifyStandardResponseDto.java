package com.team17.poc.box.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotifyStandardResponseDto {
    private String message;
    private int standardDays;
}
