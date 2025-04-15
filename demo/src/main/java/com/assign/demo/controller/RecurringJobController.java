// Update RecurringJobController to use Quartz scheduler
package com.assign.demo.controller;


import com.assign.demo.model.RecurrencePattern;
import com.assign.demo.model.ScheduledJob;
import com.assign.demo.service.QuartzSchedulerService;
import lombok.RequiredArgsConstructor;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/recurring-jobs")
@RequiredArgsConstructor
public class RecurringJobController {
   private static final Logger logger = LoggerFactory.getLogger(RecurringJobController.class);
   private final QuartzSchedulerService quartzSchedulerService;


   @PostMapping("/schedule")
   public ResponseEntity<String> scheduleRecurringJob(@RequestBody ScheduledJob job) {
       logger.info("📅 Scheduling recurring job: {}", job);
      
       // Make sure the timezone is set
       RecurrencePattern pattern = job.getRecurrence();
       if (pattern.getTimezone() == null || pattern.getTimezone().isEmpty()) {
           pattern.setTimezone(ZoneId.systemDefault().getId());
           logger.info("Setting default timezone: {}", pattern.getTimezone());
       }
      
       // Parse date string if it's coming as string from frontend
       if (pattern.getStartTime() == null && pattern.getStartTimeStr() != null) {
           try {
               // Parse using the specified timezone
               ZonedDateTime zonedDateTime = ZonedDateTime.parse(pattern.getStartTimeStr());
               pattern.setStartTime(zonedDateTime);
           } catch (DateTimeParseException e) {
               logger.error("❌ Date parsing error: {}", e.getMessage(), e);
               return ResponseEntity.badRequest().body("Invalid date/time format.");
           }
       }
      
       quartzSchedulerService.scheduleRecurringJob(job);
       return ResponseEntity.ok("✅ Recurring job scheduled for user " + job.getUserId());
   }
}
