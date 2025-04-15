package com.assign.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.apache.kafka.common.utils.Scheduler;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@EnableScheduling
@EnableConfigurationProperties(QuartzProperties.class)
@Configuration
public class QuartzConfig {
    
}