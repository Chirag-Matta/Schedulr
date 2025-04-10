// package com.assign.demo.service;

// import com.assign.demo.model.UserDetails;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;

// import java.time.Duration;
// import java.time.LocalDateTime;
// import java.util.concurrent.Executors;
// import java.util.concurrent.ScheduledExecutorService;
// import java.util.concurrent.TimeUnit;

// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class ScheduledUserFetchService {

//     private final UserService userService;
//     private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

//     public void scheduleUserFetch(Long userId, LocalDateTime scheduledTime) {
//         long delay = Duration.between(LocalDateTime.now(), scheduledTime).toMillis();
//         if (delay < 0) {
//             log.warn("⛔ Scheduled time is in the past. Skipping.");
//             return;
//         }

//         log.info("📆 Scheduling fetch for user {} in {} ms", userId, delay);
//         executor.schedule(() -> {
//             try {
//                 log.info("⏰ Scheduled fetch triggered for user {}", userId);
//                 UserDetails user = userService.getUserById(userId); // Sends to Kafka internally
//             } catch (Exception e) {
//                 log.error("❌ Failed scheduled fetch for user {}: {}", userId, e.getMessage());
//             }
//         }, delay, TimeUnit.MILLISECONDS);
//     }
// }


package com.assign.demo.service;

import com.assign.demo.model.UserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledUserFetchService {
    private final UserService userService;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
    
    public void scheduleUserFetch(Long userId, LocalDateTime scheduledTime, String timezone) {
        // Convert the scheduled time from the specified timezone to system default timezone
        ZonedDateTime zonedScheduledTime = scheduledTime.atZone(ZoneId.of(timezone));
        ZonedDateTime systemScheduledTime = zonedScheduledTime.withZoneSameInstant(ZoneId.systemDefault());
        LocalDateTime systemLocalDateTime = systemScheduledTime.toLocalDateTime();
        
        long delay = Duration.between(LocalDateTime.now(), systemLocalDateTime).toMillis();
        if (delay < 0) {
            log.warn("⛔ Scheduled time is in the past. Skipping.");
            return;
        }
        
        log.info("📆 Scheduling fetch for user {} in {} ms (timezone: {})", 
                userId, delay, timezone);
        
        executor.schedule(() -> {
            try {
                log.info("⏰ Scheduled fetch triggered for user {}", userId);
                UserDetails user = userService.getUserById(userId); // Sends to Kafka internally
            } catch (Exception e) {
                log.error("❌ Failed scheduled fetch for user {}: {}", 
                        userId, e.getMessage());
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
}