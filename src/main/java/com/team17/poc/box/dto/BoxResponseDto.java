// 제품 정보 조회 시 반환되는 응답용 DTO
package com.team17.poc.box.dto;

import com.team17.poc.box.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public class BoxResponseDto {
    private Long id;
    private String name;
    private String imageUrl;
    private String registerDate;
    private String expireDate;
    private String locationName;
    private Long locationId;
    private boolean alarmEnabled;


    public static BoxResponseDto fromEntity(Item item) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDate = item.getExpireDate() != null
                ? item.getExpireDate().format(formatter)
                : "";

        String formattedRegisterDate = item.getRegisterDate() != null
                ? item.getRegisterDate().format(formatter) : "";

        String locationName = item.getLocation() != null
                ? item.getLocation().getName()
                : "";

        return new BoxResponseDto(
                item.getId(),
                item.getName(),
                item.getImageUrl(),
                formattedRegisterDate,
                formattedDate,
                locationName,
                item.getLocation() != null ? item.getLocation().getId() : null,
                item.isAlarmEnabled()
        );
    }
}
