// Update UserController to use Quartz scheduler
package com.assign.demo.controller;

import com.assign.demo.dto.UserFetchScheduleRequest;
import com.assign.demo.model.UserDetails;
import com.assign.demo.service.KafkaProducerService;
import com.assign.demo.service.QuartzSchedulerService;
import com.assign.demo.service.UserNotFoundException;
import com.assign.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/user_details")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final KafkaProducerService kafkaProducerService;
    private final QuartzSchedulerService quartzSchedulerService;

    public UserController(UserService userService, 
                         KafkaProducerService kafkaProducerService,
                         QuartzSchedulerService quartzSchedulerService) {
        this.userService = userService;
        this.kafkaProducerService = kafkaProducerService;
        this.quartzSchedulerService = quartzSchedulerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetails> getUserById(@PathVariable Long id) {
        logger.info("Fetching user with ID: {}", id);

        try {
            UserDetails user = userService.getUserById(id);
            // Send the user data to Kafka
            kafkaProducerService.sendUserEvent(user);
            logger.info("User data sent to Kafka for ID: {}", id);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException ex) {
            logger.warn("User not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/schedule-fetch")
    public ResponseEntity<?> scheduleUserFetch(@RequestBody UserFetchScheduleRequest request) {
        logger.info("📆 Received request to schedule fetch for user {} at {}", 
                request.getUserId(), request.getScheduledTime());
                
        try {
            ZoneId zoneId = ZoneId.of(request.getTimezone());
            LocalDateTime localDateTime = LocalDateTime.parse(request.getScheduledTime());
            ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);

            
            logger.info("📆 Final scheduled time in ZonedDateTime: {}", zonedDateTime);
            
            // Schedule the job using Quartz
            quartzSchedulerService.scheduleUserFetch(request.getUserId(), zonedDateTime);
            
            return ResponseEntity.ok("✅ User fetch scheduled for " + zonedDateTime);
        } catch (DateTimeParseException e) {
            logger.error("❌ Date parsing error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Invalid date/time format.");
        } catch (Exception e) {
            logger.error("❌ Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Unexpected error occurred.");
        }
    }
}