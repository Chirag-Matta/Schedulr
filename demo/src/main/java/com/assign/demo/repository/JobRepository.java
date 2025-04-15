// package com.assign.demo.repository;

// import com.assign.demo.model.Job;
// import com.assign.demo.model.JobStatus;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;

// import java.util.List;
// import java.util.Optional;

// public interface JobRepository extends JpaRepository<Job, Long> {
//     List<Job> findByUserId(Long userId);
    
//     List<Job> findByStatus(JobStatus status);
    
//     @Query("SELECT j FROM Job j WHERE j.jobKey = :jobKey")
//     Optional<Job> findByJobKey(@Param("jobKey") String jobKey);
// }