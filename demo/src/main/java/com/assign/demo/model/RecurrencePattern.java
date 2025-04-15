// Update RecurrencePattern to use ZonedDateTime instead of LocalDateTime
package com.assign.demo.model;

import lombok.Data;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
public class RecurrencePattern {
    private Frequency frequency;
    private ZonedDateTime startTime;
    
    // Add this field to handle string input from frontend
    @JsonIgnore 
    private String startTimeStr;
    
    private String timezone;
    private List<DayOfWeek> daysOfWeek;
    private List<Integer> daysOfMonth;
    private List<Month> monthsOfYear;
    
    // Helper method to ensure timezone consistency
    public ZoneId getZoneId() {
        return ZoneId.of(timezone != null ? timezone : ZoneId.systemDefault().getId());
    }
}