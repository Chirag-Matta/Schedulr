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
//         LocalDateTime nextRun = getNextExecutionTime(job.getRecurrence());
//         long delay = Duration.between(LocalDateTime.now(), nextRun).toMillis();

//         executor.schedule(() -> {
//             try {
//                 log.info("🔁 Executing recurring job for user: {}", job.getUserId());
//                 userService.getUserById(job.getUserId()); // already sends to Kafka
//             } catch (Exception e) {
//                 log.error("❌ Error executing recurring job: {}", e.getMessage());
//             }

//             // Reschedule the next run
//             scheduleNextRun(job);
//         }, delay, TimeUnit.MILLISECONDS);
//     }

//     private LocalDateTime getNextExecutionTime(RecurrencePattern pattern) {
//         LocalDateTime now = LocalDateTime.now();
//         LocalDateTime next = pattern.getStartTime();

//         switch (pattern.getFrequency()) {
//             case HOURLY:
//                 while (!next.isAfter(now)) next = next.plusHours(1);
//                 break;
//             case DAILY:
//                 while (!next.isAfter(now)) next = next.plusDays(1);
//                 break;
//             case WEEKLY:
//                 while (!next.isAfter(now)) next = next.plusDays(1);
//                 while (!pattern.getDaysOfWeek().contains(next.getDayOfWeek())) {
//                     next = next.plusDays(1);
//                 }
//                 break;
//             case MONTHLY:
//                 while (!next.isAfter(now)) next = next.plusDays(1);
//                 while (!pattern.getDaysOfMonth().contains(next.getDayOfMonth())) {
//                     next = next.plusDays(1);
//                 }
//                 break;
//             case YEARLY:
//                 while (!next.isAfter(now)) next = next.plusYears(1);
//                 // If you want to specify months for yearly recurrence
//                 if (pattern.getMonthsOfYear() != null && !pattern.getMonthsOfYear().isEmpty()) {
//                     while (!pattern.getMonthsOfYear().contains(next.getMonth())) {
//                         next = next.plusMonths(1);
//                     }
//                 }
            
            
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
    
    // In-memory storage for jobs (you can replace this with DB later)
    private final List<ScheduledJob> recurringJobs = new ArrayList<>();
    
    public void scheduleRecurringJob(ScheduledJob job) {
        recurringJobs.add(job);
        scheduleNextRun(job);
    }
    
    private void scheduleNextRun(ScheduledJob job) {
        RecurrencePattern pattern = job.getRecurrence();
        LocalDateTime nextRun = getNextExecutionTime(pattern);
        
        // Convert the nextRun time to system time zone for scheduling
        ZonedDateTime zonedNextRun = nextRun.atZone(ZoneId.of(pattern.getTimezone() != null ? 
                pattern.getTimezone() : ZoneId.systemDefault().getId()));
        ZonedDateTime systemNextRun = zonedNextRun.withZoneSameInstant(ZoneId.systemDefault());
        LocalDateTime systemLocalNextRun = systemNextRun.toLocalDateTime();
        
        long delay = Duration.between(LocalDateTime.now(), systemLocalNextRun).toMillis();
        if (delay < 0) {
            // If the calculated next run is in the past, recalculate
            pattern.setStartTime(LocalDateTime.now());
            nextRun = getNextExecutionTime(pattern);
            zonedNextRun = nextRun.atZone(ZoneId.of(pattern.getTimezone() != null ? 
                    pattern.getTimezone() : ZoneId.systemDefault().getId()));
            systemNextRun = zonedNextRun.withZoneSameInstant(ZoneId.systemDefault());
            systemLocalNextRun = systemNextRun.toLocalDateTime();
            delay = Duration.between(LocalDateTime.now(), systemLocalNextRun).toMillis();
        }
        
        log.info("🔁 Scheduling next run for user {} at {} ({}) - in {} ms", 
                job.getUserId(), systemLocalNextRun, pattern.getTimezone(), delay);
        
        executor.schedule(() -> {
            try {
                log.info("🔁 Executing recurring job for user: {}", job.getUserId());
                userService.getUserById(job.getUserId()); // already sends to Kafka
            } catch (Exception e) {
                log.error("❌ Error executing recurring job: {}", e.getMessage());
            }
            // Reschedule the next run
            scheduleNextRun(job);
        }, delay, TimeUnit.MILLISECONDS);
    }
    
    private LocalDateTime getNextExecutionTime(RecurrencePattern pattern) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = pattern.getStartTime();
        String timezone = pattern.getTimezone() != null ? pattern.getTimezone() : ZoneId.systemDefault().getId();
        
        // Convert "now" to the target timezone for calculations
        ZonedDateTime zonedNow = now.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of(timezone));
        LocalDateTime localNow = zonedNow.toLocalDateTime();
        
        switch (pattern.getFrequency()) {
            case HOURLY:
                while (!next.isAfter(localNow)) next = next.plusHours(1);
                break;
            case DAILY:
                while (!next.isAfter(localNow)) next = next.plusDays(1);
                break;
            case WEEKLY:
                while (!next.isAfter(localNow)) next = next.plusDays(1);
                while (!pattern.getDaysOfWeek().contains(next.getDayOfWeek())) {
                    next = next.plusDays(1);
                }
                break;
            case MONTHLY:
                while (!next.isAfter(localNow)) next = next.plusDays(1);
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
}