// package com.assign.demo.controller;

// import com.assign.demo.model.Job;
// import com.assign.demo.service.JobService;
// import lombok.RequiredArgsConstructor;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @CrossOrigin(origins = "http://localhost:8080")
// @RestController
// @RequestMapping("/api/jobs")
// @RequiredArgsConstructor
// public class JobController {
//     private static final Logger logger = LoggerFactory.getLogger(JobController.class);
//     private final JobService jobService;
    
//     @GetMapping
//     public ResponseEntity<List<Job>> getAllJobs() {
//         logger.info("📋 Fetching all jobs");
//         List<Job> jobs = jobService.getAllJobs();
//         return ResponseEntity.ok(jobs);
//     }
    
//     @GetMapping("/{id}")
//     public ResponseEntity<Job> getJobById(@PathVariable Long id) {
//         logger.info("🔍 Fetching job with ID: {}", id);
//         try {
//             Job job = jobService.getJobById(id);
//             return ResponseEntity.ok(job);
//         } catch (Exception e) {
//             logger.warn("❌ Job not found with ID: {}", id);
//             return ResponseEntity.notFound().build();
//         }
//     }
    
//     @PostMapping("/{id}/execute")
//     public ResponseEntity<String> executeJob(@PathVariable Long id) {
//         logger.info("🚀 Executing job with ID: {}", id);
//         try {
//             jobService.executeJob(id);
//             return ResponseEntity.ok("✅ Job execution triggered");
//         } catch (Exception e) {
//             logger.error("❌ Failed to execute job: {}", e.getMessage(), e);
//             return ResponseEntity.badRequest().body("Failed to execute job: " + e.getMessage());
//         }
//     }
// }