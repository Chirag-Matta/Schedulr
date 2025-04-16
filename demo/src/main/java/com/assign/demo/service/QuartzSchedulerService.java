// Now, create a Quartz service to replace your current scheduling services
package com.assign.demo.service;

import com.assign.demo.model.Frequency;
import com.assign.demo.model.RecurrencePattern;
import com.assign.demo.model.ScheduledJob;
import com.assign.demo.quartz.UserFetchJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuartzSchedulerService {
    private final List<ScheduledJob> scheduledJobs = new ArrayList<>();

    private final Scheduler scheduler;

    public void scheduleUserFetch(Long userId, ZonedDateTime scheduledTime) {
        try {
            ZonedDateTime now = ZonedDateTime.now(scheduledTime.getZone());
            if (scheduledTime.isBefore(now)) {
                log.warn("⛔ Scheduled time {} is in the past. Not scheduling job for user {}", scheduledTime, userId);
                return;
            }
    
            JobDetail jobDetail = JobBuilder.newJob(UserFetchJob.class)
                    .withIdentity("userFetch-" + userId + "-" + System.currentTimeMillis())
                    .usingJobData("userId", userId)
                    .build();
    
            Date startAt = Date.from(scheduledTime.toInstant());
    
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("userFetchTrigger-" + userId + "-" + System.currentTimeMillis())
                    .startAt(startAt)
                    .build();
    
            scheduler.scheduleJob(jobDetail, trigger);
    
            log.info("📆 Scheduled one-time user fetch for user {} at {} ({})", userId, scheduledTime, scheduledTime.getZone());
    
            // ✅ Track this one-time job in scheduledJobs list
            RecurrencePattern pattern = new RecurrencePattern();
            pattern.setStartTime(scheduledTime);
            pattern.setTimezone(scheduledTime.getZone().getId());
    
            ScheduledJob oneTimeJob = new ScheduledJob();
            oneTimeJob.setUserId(userId);
            oneTimeJob.setRecurrence(pattern);
    
            scheduledJobs.add(oneTimeJob);
    
        } catch (SchedulerException e) {
            log.error("❌ Failed to schedule user fetch: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to schedule user fetch", e);
        }
    }
    
    

    public void scheduleRecurringJob(ScheduledJob job) {
        try {
            scheduledJobs.add(job); 
            RecurrencePattern pattern = job.getRecurrence();
            
            // Ensure timezone is not null and properly set
            String timezoneStr = pattern.getTimezone() != null ? 
                pattern.getTimezone() : ZoneId.systemDefault().getId();
            ZoneId zoneId = ZoneId.of(timezoneStr);
            
            // Make sure startTime is in the specified timezone
            ZonedDateTime startTime = pattern.getStartTime();
            if (startTime.getZone() == null || !startTime.getZone().equals(zoneId)) {
                // If timezone doesn't match, adjust the time to the correct timezone
                startTime = startTime.withZoneSameInstant(zoneId);
                pattern.setStartTime(startTime);
            }
            
            // Rest of your code remains the same...
            JobDetail jobDetail = JobBuilder.newJob(UserFetchJob.class)
            .withIdentity("recurringJob-" + job.getUserId(), "recurring-jobs")
            .usingJobData("userId", job.getUserId())
            .build();
        
                
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity("recurringJobTrigger-" + job.getUserId())
                .startAt(Date.from(startTime.toInstant()));
                
            ScheduleBuilder<?> scheduleBuilder = createScheduleBuilder(pattern, zoneId);
            triggerBuilder.withSchedule(scheduleBuilder);
            
            if (scheduler.checkExists(jobDetail.getKey())) {
                log.info("♻️ Job already exists: {}", jobDetail.getKey());
                return;
            }
            scheduler.scheduleJob(jobDetail, triggerBuilder.build());
            
            
            log.info("🔁 Scheduled recurring job for user {} with pattern {}", 
                job.getUserId(), pattern);
        } catch (SchedulerException e) {
            log.error("❌ Failed to schedule recurring job: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to schedule recurring job", e);
        }
    }
    
    private ScheduleBuilder<?> createScheduleBuilder(RecurrencePattern pattern, ZoneId zoneId) {
        TimeZone quartzTimeZone = TimeZone.getTimeZone(zoneId);
        
        switch (pattern.getFrequency()) {
            case HOURLY:
                SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInHours(1)
                    .repeatForever()
                    .withMisfireHandlingInstructionNextWithExistingCount();
                
                // Make sure the timezone is properly set for the trigger
                return builder;
                        
            case DAILY:
                return CronScheduleBuilder.dailyAtHourAndMinute(
                        pattern.getStartTime().getHour(), 
                        pattern.getStartTime().getMinute())
                        .inTimeZone(quartzTimeZone);
                        
            case WEEKLY:
                if (pattern.getDaysOfWeek() == null || pattern.getDaysOfWeek().isEmpty()) {
                    // If no days specified, use the day of week from start time
                    DayOfWeek dayOfWeek = pattern.getStartTime().getDayOfWeek();
                    return CronScheduleBuilder.weeklyOnDayAndHourAndMinute(
                            mapDayOfWeekToQuartz(dayOfWeek),
                            pattern.getStartTime().getHour(),
                            pattern.getStartTime().getMinute())
                            .inTimeZone(quartzTimeZone);
                } else {
                    // For multiple days, we need to create a cron expression
                    String cronExpression = createWeeklyCronExpression(pattern);
                    return CronScheduleBuilder.cronSchedule(cronExpression)
                            .inTimeZone(quartzTimeZone);
                }
                
            case MONTHLY:
                if (pattern.getDaysOfMonth() == null || pattern.getDaysOfMonth().isEmpty()) {
                    // If no days specified, use the day from start time
                    return CronScheduleBuilder.monthlyOnDayAndHourAndMinute(
                            pattern.getStartTime().getDayOfMonth(),
                            pattern.getStartTime().getHour(),
                            pattern.getStartTime().getMinute())
                            .inTimeZone(quartzTimeZone);
                } else {
                    // For multiple days, we need to create a cron expression
                    String cronExpression = createMonthlyCronExpression(pattern);
                    return CronScheduleBuilder.cronSchedule(cronExpression)
                            .inTimeZone(quartzTimeZone);
                }
            
            // Handle YEARLY case in createScheduleBuilder
            case YEARLY:
    if (pattern.getDaysOfMonth() == null || pattern.getMonthsOfYear() == null ||
        pattern.getDaysOfMonth().isEmpty() || pattern.getMonthsOfYear().isEmpty()) {
        throw new IllegalArgumentException("Yearly jobs require both daysOfMonth and monthsOfYear.");
    }

    StringBuilder months = new StringBuilder();
    for (Month m : pattern.getMonthsOfYear()) {
        if (months.length() > 0) months.append(",");
        months.append(m.getValue());
    }

    StringBuilder days = new StringBuilder();
    for (Integer d : pattern.getDaysOfMonth()) {
        if (days.length() > 0) days.append(",");
        days.append(d);
    }

    String yearlyCron = String.format("0 %d %d %s %s ?",
        pattern.getStartTime().getMinute(),
        pattern.getStartTime().getHour(),
        days.toString(),
        months.toString()
    );

    return CronScheduleBuilder.cronSchedule(yearlyCron).inTimeZone(TimeZone.getTimeZone(zoneId));

            default:
                throw new IllegalArgumentException("Unsupported frequency: " + pattern.getFrequency());
        }
    }
    
    private int mapDayOfWeekToQuartz(DayOfWeek dayOfWeek) {
        // Java DayOfWeek: MON=1, TUE=2, ... SUN=7
        // Quartz: SUN=1, MON=2, ... SAT=7
        int javaValue = dayOfWeek.getValue();
        return javaValue == 7 ? 1 : javaValue + 1;
    }
    
    private String createWeeklyCronExpression(RecurrencePattern pattern) {
        // Cron format: seconds minutes hours day-of-month month day-of-week year
        StringBuilder daysOfWeekStr = new StringBuilder();
        
        for (DayOfWeek day : pattern.getDaysOfWeek()) {
            if (daysOfWeekStr.length() > 0) {
                daysOfWeekStr.append(",");
            }
            // Convert Java DayOfWeek (1-7, Monday=1) to Quartz (1-7, Sunday=1)
            int quartzDay = day.getValue() % 7 + 1;
            daysOfWeekStr.append(quartzDay);
        }
        
        return String.format("0 %d %d ? * %s",
                pattern.getStartTime().getMinute(),
                pattern.getStartTime().getHour(),
                daysOfWeekStr.toString());
    }
    
    private String createMonthlyCronExpression(RecurrencePattern pattern) {
        // Cron format: seconds minutes hours day-of-month month day-of-week year
        StringBuilder daysOfMonthStr = new StringBuilder();
        
        for (Integer day : pattern.getDaysOfMonth()) {
            if (daysOfMonthStr.length() > 0) {
                daysOfMonthStr.append(",");
            }
            daysOfMonthStr.append(day);
        }
        
        return String.format("0 %d %d %s * ?",
                pattern.getStartTime().getMinute(),
                pattern.getStartTime().getHour(),
                daysOfMonthStr.toString());
    }

    public List<ScheduledJob> getScheduledJobs() {
        List<ScheduledJob> jobs = new ArrayList<>();
    
        try {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.anyGroup())) {
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                JobDataMap dataMap = jobDetail.getJobDataMap();
    
                if (!dataMap.containsKey("userId")) continue;
    
                Long userId = dataMap.getLong("userId");
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
    
                if (triggers.isEmpty()) continue;
    
                Trigger trigger = triggers.get(0);
                Date nextFireTime = trigger.getNextFireTime();
    
                RecurrencePattern pattern = new RecurrencePattern();
                pattern.setStartTime(ZonedDateTime.ofInstant(nextFireTime.toInstant(), ZoneId.systemDefault()));
                pattern.setTimezone(ZoneId.systemDefault().getId());
    
                ScheduledJob job = new ScheduledJob();
                job.setUserId(userId);
                job.setRecurrence(pattern);
    
                jobs.add(job);

                return new ArrayList<>(scheduledJobs);
            }
        } catch (SchedulerException e) {
            log.error("❌ Failed to fetch scheduled jobs: {}", e.getMessage(), e);
        }
    
        return jobs;
    }

    public boolean deleteJobByUserId(Long userId) {
        // 🟢 Only remove from internal list, no Quartz operations
        boolean removed = scheduledJobs.removeIf(job -> job.getUserId().equals(userId));
        if (removed) {
            log.info("🗑️ Deleted job for user {} from scheduledJobs list only", userId);
        } else {
            log.warn("⚠️ No job found in scheduledJobs list for user {}", userId);
        }
        return removed;
    }
    
    
    
    
    
}

