package com.assign.demo.model;

import lombok.Data;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Data
public class RecurrencePattern {
    private Frequency frequency;
    private LocalDateTime startTime;
    private List<DayOfWeek> daysOfWeek; // for weekly jobs
    private List<Integer> daysOfMonth; // for monthly jobs
    private List<Month> monthsOfYear; // Add this field for month selection
    private String timezone; // Add timezone field
}