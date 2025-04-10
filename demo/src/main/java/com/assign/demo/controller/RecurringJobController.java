package com.assign.demo.controller;

import com.assign.demo.model.ScheduledJob;
import com.assign.demo.service.RecurringJobSchedulerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recurring-jobs")
@RequiredArgsConstructor
public class RecurringJobController {

    private static final Logger logger = LoggerFactory.getLogger(RecurringJobController.class);

    private final RecurringJobSchedulerService recurringJobSchedulerService;

    @PostMapping("/schedule")
    public ResponseEntity<String> scheduleRecurringJob(@RequestBody ScheduledJob job) {
        logger.info("📅 Scheduling recurring job: {}", job);
        recurringJobSchedulerService.scheduleRecurringJob(job);
        return ResponseEntity.ok("✅ Recurring job scheduled for user " + job.getUserId());
    }
}
