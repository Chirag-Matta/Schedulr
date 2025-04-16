package com.assign.demo.controller;

import com.assign.demo.model.Frequency;
import com.assign.demo.model.RecurrencePattern;
import com.assign.demo.model.ScheduledJob;
import com.assign.demo.service.QuartzSchedulerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RecurringJobControllerTest {

    @Mock
    private QuartzSchedulerService quartzSchedulerService;

    @InjectMocks
    private RecurringJobController recurringJobController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(recurringJobController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For handling ZonedDateTime
    }

    @Test
    void scheduleRecurringJob_Success() throws Exception {
        ScheduledJob job = new ScheduledJob();
        job.setUserId(1L);
        
        RecurrencePattern pattern = new RecurrencePattern();
        pattern.setFrequency(Frequency.DAILY);
        pattern.setStartTimeStr("2025-05-01T10:00:00Z");
        
        job.setRecurrence(pattern);

        doNothing().when(quartzSchedulerService).scheduleRecurringJob(any(ScheduledJob.class));

        mockMvc.perform(post("/api/recurring-jobs/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(job)))
                .andExpect(status().isOk())
                .andExpect(content().string("✅ Recurring job scheduled for user 1"));

        verify(quartzSchedulerService).scheduleRecurringJob(any(ScheduledJob.class));
    }

    @Test
    void scheduleRecurringJob_WithZonedDateTime() throws Exception {
        ScheduledJob job = new ScheduledJob();
        job.setUserId(1L);
        
        RecurrencePattern pattern = new RecurrencePattern();
        pattern.setFrequency(Frequency.DAILY);
        pattern.setStartTime(ZonedDateTime.now().plusDays(1));
        pattern.setTimezone(ZoneId.systemDefault().getId());
        
        job.setRecurrence(pattern);

        doNothing().when(quartzSchedulerService).scheduleRecurringJob(any(ScheduledJob.class));

        mockMvc.perform(post("/api/recurring-jobs/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(job)))
                .andExpect(status().isOk())
                .andExpect(content().string("✅ Recurring job scheduled for user 1"));

        verify(quartzSchedulerService).scheduleRecurringJob(any(ScheduledJob.class));
    }
}