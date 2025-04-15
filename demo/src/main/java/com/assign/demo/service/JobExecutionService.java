// package com.assign.demo.service;

// import com.assign.demo.model.Job;
// import com.assign.demo.model.JobStatus;
// import com.assign.demo.repository.JobRepository;
// import org.quartz.JobExecutionContext;
// import org.quartz.JobExecutionException;
// import org.quartz.JobKey;
// import org.quartz.Scheduler;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.time.ZonedDateTime;

// @Service
// public class JobExecutionService {

//     @Autowired
//     private JobRepository jobRepository;
    
//     @Autowired
//     private Scheduler scheduler;
    
//     public void executeJobNow(Long jobId) {
//         try {
//             // Find the job in the database
//             Job job = jobRepository.findById(jobId)
//                 .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));
            
//             // Get the Quartz job key
//             JobKey jobKey = JobKey.jobKey(job.getJobKey());
            
//             // Trigger the job to run immediately
//             scheduler.triggerJob(jobKey);
            
//             // Update job status in database
//             job.setLastExecutionTime(ZonedDateTime.now());
//             job.setStatus(JobStatus.PENDING);
//             jobRepository.save(job);
            
//         } catch (Exception e) {
//             throw new RuntimeException("Failed to execute job: " + e.getMessage(), e);
//         }
//     }
// }