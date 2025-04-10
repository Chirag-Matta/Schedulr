package com.assign.demo.model;

import lombok.Data;

@Data
public class ScheduledJob {
    private Long userId;
    private RecurrencePattern recurrence;
}
