package com.assign.demo.dto;

public class UserFetchScheduleRequest {

    private Long userId;
    private String scheduledTime; // Should be in format: "yyyy-MM-dd'T'HH:mm:ss"
    
    private String timezone;      // Should be in format: "America/New_York"

    // ✅ Getters
    public Long getUserId() {
        return userId;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public String getTimezone() {
        return timezone;
    }

    // ✅ Setters
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
