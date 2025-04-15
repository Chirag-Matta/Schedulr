// package com.assign.demo.service;

// import com.assign.demo.model.Job;
// import com.assign.demo.model.JobStatus;
// import com.assign.demo.repository.JobRepository;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;

// import java.time.ZonedDateTime;
// import java.util.List;

// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class JobService {
//     private final JobRepository jobRepository;
//     private final QuartzSchedulerService quartzSchedulerService;
//     private final ObjectMapper objectMapper;
    
//     public List<Job> getAllJobs() {
//         return jobRepository.findAll();
//     }
    
//     public Job getJobById(Long id) {
//         return jobRepository.findById(id)
//             .orElseThrow(() -> new RuntimeException("Job not found with ID: " + id));
//     }
    
//     public Job saveJob(Job job) {
//         return jobRepository.save(job);
//     }
    
//     public void updateJobStatus(Long jobId, JobStatus status) {
//         Job job = getJobById(jobId);
//         job.setStatus(status);
        
//         if (status == JobStatus.COMPLETED || status == JobStatus.FAILED) {
//             job.setLastExecutionTime(ZonedDateTime.now());
//         }
        
//         jobRepository.save(job);
//     }
    
//     public void updateJobStatus(Long jobId, JobStatus status, String errorMessage) {
//         Job job = getJobById(jobId);
//         job.setStatus(status);
//         job.setErrorMessage(errorMessage);
        
//         if (status == JobStatus.COMPLETED || status == JobStatus.FAILED) {
//             job.setLastExecutionTime(ZonedDateTime.now());
//         }
        
//         jobRepository.save(job);
//     }
    
//     public void executeJob(Long jobId) {
//         Job job = getJobById(jobId);
        
//         if (job.getStatus() != JobStatus.PENDING) {
//             throw new IllegalStateException("Only pending jobs can be executed");
//         }
        
//         // Trigger execution based on job type
//         // quartzSchedulerService.executeJobNow(job.getJobKey());
        
//         // Update status to indicate it's been triggered
//         job.setStatus(JobStatus.PENDING); // Keeps it pending until completion
//         jobRepository.save(job);
        
//         log.info("🚀 Triggered immediate execution of job: {}", jobId);
//     }
// }