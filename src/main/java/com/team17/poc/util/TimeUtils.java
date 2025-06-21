package com.team17.poc.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtils {
    public static String toTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());

        if (duration.toMinutes() < 1) return "방금 전";
        if (duration.toMinutes() < 60) return duration.toMinutes() + "분 전";
        if (duration.toHours() < 24) return duration.toHours() + "시간 전";
        if (duration.toDays() < 7) return duration.toDays() + "일 전";

        return createdAt.toLocalDate().toString(); // "2025-06-18"
    }
}
