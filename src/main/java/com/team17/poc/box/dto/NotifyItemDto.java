package com.team17.poc.box.dto;

import com.team17.poc.box.entity.Item;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@Builder
public class NotifyItemDto {
    private Long itemId;
    private String name;
    private String locationName;
    private LocalDate expireDate;
    private long daysLeft;

    /**
     * ✅ Item → NotifyItemDto 변환
     * 유통기한까지 남은 일수 계산 포함
     */
    public static NotifyItemDto from(Item item) {
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), item.getExpireDate());
        return NotifyItemDto.builder()
                .itemId(item.getId())
                .name(item.getName())
                .locationName(item.getLocation().getName())
                .expireDate(item.getExpireDate())
                .daysLeft(daysLeft)
                .build();
    }
}
