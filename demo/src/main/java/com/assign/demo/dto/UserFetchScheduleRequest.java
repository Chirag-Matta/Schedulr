package com.assign.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserFetchScheduleRequest {
    private Long userId;
    private LocalDateTime scheduledTime;
    private String timezone;
}