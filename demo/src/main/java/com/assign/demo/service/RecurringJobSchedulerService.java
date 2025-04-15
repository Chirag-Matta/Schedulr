// package com.assign.demo.service;

// import com.assign.demo.model.*;

// import jakarta.annotation.PostConstruct;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;

// import java.time.*;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.concurrent.*;

// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class RecurringJobSchedulerService {

//     private final UserService userService;

//     private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

//     // In-memory storage for jobs (you can replace this with DB later)
//     private final List<ScheduledJob> recurringJobs = new ArrayList<>();

//     public void scheduleRecurringJob(ScheduledJob job) {
//         recurringJobs.add(job);
//         scheduleNextRun(job);
//     }

//     private void scheduleNextRun(ScheduledJob job) {
//         RecurrencePattern pattern = job.getRecurrence();
//         ZoneId zoneId = ZoneId.of(pattern.getTimezone());
//         ZonedDateTime startTime = pattern.getStartTime().withZoneSameInstant(zoneId);
// pattern.setStartTime(startTime);
    
//         // Ensure startTime is interpreted in the correct zone
//         if (!startTime.getZone().equals(zoneId)) {
//             startTime = startTime.withZoneSameInstant(zoneId);
//             pattern.setStartTime(startTime);
//         }
    
//         ZonedDateTime nowInZone = ZonedDateTime.now(ZoneId.of(pattern.getTimezone()));
//         ZonedDateTime nextRun = getNextExecutionTime(pattern, nowInZone);
    
//         ZonedDateTime systemNow = ZonedDateTime.now(ZoneId.systemDefault());
//         ZonedDateTime systemNextRun = nextRun.withZoneSameInstant(ZoneId.systemDefault());
    
//         long delay = Duration.between(systemNow, systemNextRun).toMillis();
    
//         // Log full scheduling details
//         log.info("📝 Schedule Info for user {}:", job.getUserId());
//         log.info("⏰ Now ({}): {}", zoneId, nowInZone);
//         log.info("📅 Start Time: {}", startTime);
//         log.info("🔜 Next Execution Time ({}): {}", zoneId, nextRun);
//         log.info("🕓 Converted to System Time ({}): {}", ZoneId.systemDefault(), systemNextRun);
//         log.info("🧮 Delay in ms: {}", delay);
    
//         if (delay < 0) {
//             log.warn("⚠ Delay is negative. Rescheduling from now.");
    
//             pattern.setStartTime(ZonedDateTime.now(zoneId));
//             nextRun = getNextExecutionTime(pattern, ZonedDateTime.now(zoneId));
//             systemNextRun = nextRun.withZoneSameInstant(ZoneId.systemDefault());
//             delay = Duration.between(ZonedDateTime.now(ZoneId.systemDefault()), systemNextRun).toMillis();
    
//             log.info("🔁 Adjusted Next Execution Time: {}", systemNextRun);
//             log.info("🧮 Recomputed Delay: {}", delay);
//         }
    
//         executor.schedule(() -> {
//             ZonedDateTime actualExecutionTime = ZonedDateTime.now(ZoneId.systemDefault());
    
//             log.info("🚀 Job Triggered for user {}!", job.getUserId());
//             log.info("🕒 Actual Execution Time (system): {}", actualExecutionTime);
//             log.info("🌍 Actual Execution Time in original timezone ({}): {}",
//                      zoneId, actualExecutionTime.withZoneSameInstant(zoneId));
    
//             try {
//                 userService.getUserById(job.getUserId());
//             } catch (Exception e) {
//                 log.error("❌ Error executing job: {}", e.getMessage(), e);
//             }
    
//             // Reschedule the next run
//             scheduleNextRun(job);
//         }, delay, TimeUnit.MILLISECONDS);
//     }
    
    

//     private ZonedDateTime getNextExecutionTime(RecurrencePattern pattern, ZonedDateTime now) {
//         ZonedDateTime next = pattern.getStartTime().withZoneSameInstant(now.getZone());
//         Frequency frequency = pattern.getFrequency();
    
//         while (!next.isAfter(now)) {
//             switch (frequency) {
//                 case HOURLY:
//                     next = next.plusHours(1);
//                     break;
    
//                 case DAILY:
//                     next = next.plusDays(1);
//                     break;
    
//                 case WEEKLY:
//                     next = next.plusDays(1);
//                     while (!pattern.getDaysOfWeek().contains(next.getDayOfWeek())) {
//                         next = next.plusDays(1);
//                     }
//                     break;
    
//                 case MONTHLY:
//                     next = next.plusDays(1);
//                     while (!pattern.getDaysOfMonth().contains(next.getDayOfMonth())) {
//                         next = next.plusDays(1);
//                     }
//                     break;
    
//                 case YEARLY:
//                     next = next.plusDays(1);
//                     while (
//                         !pattern.getDaysOfMonth().contains(next.getDayOfMonth()) ||
//                         !pattern.getMonthsOfYear().contains(next.getMonth())
//                     ) {
//                         next = next.plusDays(1);
//                     }
//                     break;
    
//                 default:
//                     throw new IllegalArgumentException("Unsupported frequency: " + frequency);
//             }
//         }
    
//         return next;
//     }
    
    
    

//     @PostConstruct
//     public void loadRecurringJobsOnStartup() {
//         log.info("🔄 Scheduler started with {} recurring job(s)", recurringJobs.size());
//         for (ScheduledJob job : recurringJobs) {
//             scheduleNextRun(job);
//         }
//     }
// }


package com.assign.demo.service;

import com.assign.demo.model.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecurringJobSchedulerService {
    private final UserService userService;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
    
    
    private final List<ScheduledJob> recurringJobs = new ArrayList<>();
    
    public void scheduleRecurringJob(ScheduledJob job) {
        recurringJobs.add(job);
        scheduleNextRun(job);
    }
    
    private void scheduleNextRun(ScheduledJob job) {
        RecurrencePattern pattern = job.getRecurrence();
        ZonedDateTime nextRun = getNextExecutionTime(pattern);
        
        // Convert to system time zone for scheduling
        ZonedDateTime systemNextRun = nextRun.withZoneSameInstant(ZoneId.systemDefault());
        
        long delay = Duration.between(ZonedDateTime.now(ZoneId.systemDefault()), systemNextRun).toMillis();
        if (delay < 0) {
            // If calculated time is in the past, reschedule from now
            pattern.setStartTime(ZonedDateTime.now(ZoneId.of(pattern.getTimezone())));
            nextRun = getNextExecutionTime(pattern);
            systemNextRun = nextRun.withZoneSameInstant(ZoneId.systemDefault());
            delay = Duration.between(ZonedDateTime.now(ZoneId.systemDefault()), systemNextRun).toMillis();
        }
        
        log.info("🔁 Scheduling next run for user {} at {} ({}) - in {} ms", 
                job.getUserId(), systemNextRun, pattern.getTimezone(), delay);
        
        executor.schedule(() -> {
            try {
                log.info("🔁 Executing recurring job for user: {}", job.getUserId());
                userService.getUserById(job.getUserId());
            } catch (Exception e) {
                log.error("❌ Error executing recurring job: {}", e.getMessage());
            }
            // Reschedule the next run
            scheduleNextRun(job);
        }, delay, TimeUnit.MILLISECONDS);
    }
    
    private ZonedDateTime getNextExecutionTime(RecurrencePattern pattern) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(pattern.getTimezone() != null ? 
                pattern.getTimezone() : ZoneId.systemDefault().getId()));
        ZonedDateTime next = pattern.getStartTime();
        
        switch (pattern.getFrequency()) {
            case HOURLY:
                while (!next.isAfter(now)) next = next.plusHours(1);
                break;
            case DAILY:
                while (!next.isAfter(now)) next = next.plusDays(1);
                break;
            case WEEKLY:
                while (!next.isAfter(now)) next = next.plusDays(1);
                while (!pattern.getDaysOfWeek().contains(next.getDayOfWeek())) {
                    next = next.plusDays(1);
                }
                break;
            case MONTHLY:
                while (!next.isAfter(now)) next = next.plusDays(1);
                while (!pattern.getDaysOfMonth().contains(next.getDayOfMonth())) {
                    next = next.plusDays(1);
                }
                break;
        }
        return next;
    }
    
    @PostConstruct
    public void loadRecurringJobsOnStartup() {
        log.info("🔄 Scheduler started with {} recurring job(s)", recurringJobs.size());
        for (ScheduledJob job : recurringJobs) {
            scheduleNextRun(job);
        }
    }

    public ZonedDateTime getNextExecutionTime(RecurrencePattern pattern, ZonedDateTime now) {
    ZonedDateTime next = pattern.getStartTime().withZoneSameInstant(now.getZone());
    Frequency frequency = pattern.getFrequency();

    while (!next.isAfter(now)) {
        switch (frequency) {
            case HOURLY:
                next = next.plusHours(1);
                break;
            case DAILY:
                next = next.plusDays(1);
                break;
            case WEEKLY:
                next = next.plusDays(1);
                while (!pattern.getDaysOfWeek().contains(next.getDayOfWeek())) {
                    next = next.plusDays(1);
                }
                break;
            case MONTHLY:
                next = next.plusDays(1);
                while (!pattern.getDaysOfMonth().contains(next.getDayOfMonth())) {
                    next = next.plusDays(1);
                }
                break;
            case YEARLY:
                next = next.plusDays(1);
                while (!pattern.getDaysOfMonth().contains(next.getDayOfMonth()) ||
                       !pattern.getMonthsOfYear().contains(next.getMonth())) {
                    next = next.plusDays(1);
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported frequency: " + frequency);
        }
    }

    return next;
}

}