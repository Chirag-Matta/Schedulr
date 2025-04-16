package com.assign.demo.service;

import com.assign.demo.model.Frequency;
import com.assign.demo.model.RecurrencePattern;
import com.assign.demo.model.ScheduledJob;
import com.assign.demo.quartz.UserFetchJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuartzSchedulerServiceTest {

    @Mock
    private Scheduler scheduler;

    @InjectMocks
    private QuartzSchedulerService quartzSchedulerService;

    private ZonedDateTime futureTime;
    private ZonedDateTime pastTime;

    @BeforeEach
    void setUp() {
        futureTime = ZonedDateTime.now().plusHours(1);
        pastTime = ZonedDateTime.now().minusHours(1);
    }

    @Test
    void scheduleUserFetch_Success() throws SchedulerException {
        doNothing().when(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));

        quartzSchedulerService.scheduleUserFetch(1L, futureTime);

        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void scheduleUserFetch_PastTime_NoScheduling() throws SchedulerException {
        quartzSchedulerService.scheduleUserFetch(1L, pastTime);

        verify(scheduler, never()).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void scheduleRecurringJob_Hourly() throws SchedulerException {
        ScheduledJob job = new ScheduledJob();
        job.setUserId(1L);
        
        RecurrencePattern pattern = new RecurrencePattern();
        pattern.setFrequency(Frequency.HOURLY);
        pattern.setStartTime(futureTime);
        pattern.setTimezone(ZoneId.systemDefault().getId());
        
        job.setRecurrence(pattern);

        doNothing().when(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));

        quartzSchedulerService.scheduleRecurringJob(job);

        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void scheduleRecurringJob_Daily() throws SchedulerException {
        ScheduledJob job = new ScheduledJob();
        job.setUserId(1L);
        
        RecurrencePattern pattern = new RecurrencePattern();
        pattern.setFrequency(Frequency.DAILY);
        pattern.setStartTime(futureTime);
        pattern.setTimezone(ZoneId.systemDefault().getId());
        
        job.setRecurrence(pattern);

        doNothing().when(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));

        quartzSchedulerService.scheduleRecurringJob(job);

        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void scheduleRecurringJob_Weekly() throws SchedulerException {
        ScheduledJob job = new ScheduledJob();
        job.setUserId(1L);
        
        RecurrencePattern pattern = new RecurrencePattern();
        pattern.setFrequency(Frequency.WEEKLY);
        pattern.setStartTime(futureTime);
        pattern.setTimezone(ZoneId.systemDefault().getId());
        pattern.setDaysOfWeek(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY));
        
        job.setRecurrence(pattern);

        doNothing().when(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));

        quartzSchedulerService.scheduleRecurringJob(job);

        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void scheduleRecurringJob_Monthly() throws SchedulerException {
        ScheduledJob job = new ScheduledJob();
        job.setUserId(1L);
        
        RecurrencePattern pattern = new RecurrencePattern();
        pattern.setFrequency(Frequency.MONTHLY);
        pattern.setStartTime(futureTime);
        pattern.setTimezone(ZoneId.systemDefault().getId());
        pattern.setDaysOfMonth(Arrays.asList(1, 15));
        
        job.setRecurrence(pattern);

        doNothing().when(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));

        quartzSchedulerService.scheduleRecurringJob(job);

        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }
    
    @Test
    void scheduleRecurringJob_SchedulerException() throws SchedulerException {
        ScheduledJob job = new ScheduledJob();
        job.setUserId(1L);
        
        RecurrencePattern pattern = new RecurrencePattern();
        pattern.setFrequency(Frequency.DAILY);
        pattern.setStartTime(futureTime);
        pattern.setTimezone(ZoneId.systemDefault().getId());
        
        job.setRecurrence(pattern);

        doThrow(new SchedulerException("Test exception")).when(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));

        try {
            quartzSchedulerService.scheduleRecurringJob(job);
        } catch (RuntimeException e) {
            // Expected exception
        }

        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }
}