// First, create a Quartz job that will fetch user data
package com.assign.demo.quartz;

import com.assign.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.sql.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserFetchJob implements Job {

    @Autowired
    private UserService userService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();
        Long userId = dataMap.getLong("userId");
        
        log.info("⏰ Starting scheduled user fetch job for user: {}, scheduled time: {}, current time: {}", 
    userId, context.getScheduledFireTime(), new Date(userId));
        
        try {
            userService.getUserById(userId);
            log.info("✅ Successfully fetched user data for user: {}", userId);
        } catch (Exception e) {
            log.error("❌ Failed to fetch user data for user: {}: {}", userId, e.getMessage());
            throw new JobExecutionException(e);
        }
    }
}

