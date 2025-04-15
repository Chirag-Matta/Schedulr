// package com.assign.demo.model;

// import jakarta.persistence.*;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor;

// import java.time.ZonedDateTime;

// @Entity
// @Table(name = "jobs")
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// public class Job {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;
    
//     private String jobType; // "ONE_TIME" or "RECURRING"
    
//     @Column(name = "job_key")
//     private String jobKey; // Quartz job key for reference
    
//     private Long userId;
    
//     private ZonedDateTime scheduledTime;
    
//     private ZonedDateTime lastExecutionTime;
    
//     private String timezone;
    
//     @Enumerated(EnumType.STRING)
//     private JobStatus status;
    
//     private String errorMessage;
    
//     // For recurring jobs
//     @Column(columnDefinition = "TEXT")
//     private String recurrencePattern; // JSON representation of RecurrencePattern
// }