// 제품 등록 및 수정 시의 JSON 형태 정의하는 페이지
package com.team17.poc.box.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ItemRequestDto {

    private String name;
    private String imageUrl;
    private LocalDate registerDate;
    private LocalDate expireDate;
    private boolean alarmEnabled;
    private Long locationId;

}
