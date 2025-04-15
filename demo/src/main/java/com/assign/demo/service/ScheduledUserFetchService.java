package com.assign.demo.service;

import com.assign.demo.model.UserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledUserFetchService {

    private final UserService userService;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    public void scheduleUserFetch(Long userId, LocalDateTime scheduledTime) {
        long delay = Duration.between(LocalDateTime.now(), scheduledTime).toMillis();
        if (delay < 0) {
            log.warn("⛔ Scheduled time is in the past. Skipping.");
            return;
        }

        log.info("📆 Scheduling fetch for user {} in {} ms", userId, delay);
        executor.schedule(() -> {
            try {
                log.info("⏰ Scheduled fetch triggered for user {}", userId);
                UserDetails user = userService.getUserById(userId); // Sends to Kafka internally
            } catch (Exception e) {
                log.error("❌ Failed scheduled fetch for user {}: {}", userId, e.getMessage());
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
}


// package com.assign.demo.service;

// import com.assign.demo.model.UserDetails;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;
// import java.time.Duration;
// import java.time.LocalDateTime;
// import java.time.ZoneId;
// import java.time.ZonedDateTime;
// import java.util.concurrent.Executors;
// import java.util.concurrent.ScheduledExecutorService;
// import java.util.concurrent.TimeUnit;

// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class ScheduledUserFetchService {
//     private final UserService userService;
//     private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
    
//     public void scheduleUserFetch(Long userId, ZonedDateTime scheduledTime, String timezone) {
//         // The time is already in the correct timezone
//         // Convert to system timezone for scheduling
//         ZonedDateTime systemScheduledTime = scheduledTime.withZoneSameInstant(ZoneId.systemDefault());
        
//         long delay = Duration.between(ZonedDateTime.now(), systemScheduledTime).toMillis();
        
//         if (delay < 0) {
//             log.warn("⛔ Scheduled time is in the past. Skipping.");
//             return;
//         }
    
//         log.info("📆 Scheduling fetch for user {} at {} ({}) - in {} ms", 
//                  userId, scheduledTime, timezone, delay);
        
//         executor.schedule(() -> {
//             try {
//                 log.info("⏰ Scheduled fetch triggered for user {}", userId);
//                 UserDetails user = userService.getUserById(userId);
//                 // Sends to Kafka internally
//             } catch (Exception e) {
//                 log.error("❌ Failed scheduled fetch for user {}: {}", userId, e.getMessage());
//             }
//         }, delay, TimeUnit.MILLISECONDS);
//     }

//     public void scheduleUserFetchExplicit(Long userId, ZonedDateTime zonedScheduledTime) {
//         // Convert to system timezone for scheduling
//         ZonedDateTime systemTime = zonedScheduledTime.withZoneSameInstant(ZoneId.systemDefault());
        
//         // Calculate delay from current system time
//         ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
//         long delay = Duration.between(now, systemTime).toMillis();
        
//         if (delay < 0) {
//             log.warn("⛔ Scheduled time is in the past: {} (system time: {}). Skipping.", 
//                      zonedScheduledTime, systemTime);
//             return;
//         }
    
//         log.info("📆 Scheduling fetch for user {} at original time: {}", userId, zonedScheduledTime);
//         log.info("📆 System time for scheduling: {}, delay: {} ms", systemTime, delay);
        
//         executor.schedule(() -> {
//             try {
//                 log.info("⏰ Scheduled fetch triggered for user {}", userId);
//                 UserDetails user = userService.getUserById(userId);
//                 // Sends to Kafka internally
//             } catch (Exception e) {
//                 log.error("❌ Failed scheduled fetch for user {}: {}", userId, e.getMessage());
//             }
//         }, delay, TimeUnit.MILLISECONDS);
//     }

// }