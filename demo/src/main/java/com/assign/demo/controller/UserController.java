// package com.assign.demo.controller;

// import com.assign.demo.model.UserDetails;
// import com.assign.demo.service.UserNotFoundException;
// import com.assign.demo.service.UserService;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/user_details")
// public class UserController {

//     private static final Logger logger = LoggerFactory.getLogger(UserController.class);
//     private final UserService userService;

//     public UserController(UserService userService) {
//         this.userService = userService;
//     }

//     // 📥 READ: Get user by ID
//     @GetMapping("/{id}")
//     public ResponseEntity<UserDetails> getUserById(@PathVariable Long id) {
//         logger.info("Fetching user with ID: {}", id);
//         try {
//             UserDetails user = userService.getUserById(id);
//             return ResponseEntity.ok(user);
//         } catch (UserNotFoundException ex) {
//             logger.warn("User not found with ID: {}", id);
//             return ResponseEntity.notFound().build();
//         }
//     }

//     // 🆕 CREATE: Create a new user
//     @PostMapping
//     public ResponseEntity<UserDetails> createUser(@RequestBody UserDetails user) {
//         logger.info("Creating new user: {}", user);
//         UserDetails created = userService.createUser(user);
//         return ResponseEntity.ok(created);
//     }

//     // 🔄 UPDATE: Update existing user
//     @PutMapping("/{id}")
//     public ResponseEntity<UserDetails> updateUser(@PathVariable Long id, @RequestBody UserDetails updatedUser) {
//         logger.info("Updating user with ID: {}", id);
//         try {
//             UserDetails user = userService.updateUser(id, updatedUser);
//             return ResponseEntity.ok(user);
//         } catch (UserNotFoundException ex) {
//             logger.warn("User not found with ID: {}", id);
//             return ResponseEntity.notFound().build();
//         }
//     }

//     // 🗑 DELETE: Delete a user
//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//         logger.info("Deleting user with ID: {}", id);
//         try {
//             userService.deleteUser(id);
//             return ResponseEntity.noContent().build(); // 204
//         } catch (UserNotFoundException ex) {
//             logger.warn("User not found with ID: {}", id);
//             return ResponseEntity.notFound().build();
//         }
//     }
// }


package com.assign.demo.controller;

import com.assign.demo.dto.UserFetchScheduleRequest;
import com.assign.demo.model.UserDetails;
import com.assign.demo.service.KafkaProducerService;
import com.assign.demo.service.ScheduledUserFetchService;
import com.assign.demo.service.UserNotFoundException;
import com.assign.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user_details")

public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final KafkaProducerService kafkaProducerService;
    private final ScheduledUserFetchService scheduledUserFetchService;

    public UserController(UserService userService, KafkaProducerService kafkaProducerService,
                        ScheduledUserFetchService scheduledUserFetchService) {
        this.userService = userService;
        this.kafkaProducerService = kafkaProducerService;
        this.scheduledUserFetchService = scheduledUserFetchService;
    }

    // public UserController(UserService userService, KafkaProducerService kafkaProducerService) {
    //     this.userService = userService;
    //     this.kafkaProducerService = kafkaProducerService;
    // }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetails> getUserById(@PathVariable Long id) {
        logger.info("Fetching user with ID: {}", id);
        try {
            UserDetails user = userService.getUserById(id);
            
            // Send the user data to Kafka
            kafkaProducerService.sendUserEvent(user);
            logger.info("User data sent to Kafka for ID: {}", id);
            
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException ex) {
            logger.warn("User not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    // @PostMapping("/schedule-fetch")
    // public ResponseEntity<String> scheduleUserFetch(@RequestBody UserFetchScheduleRequest request) {
    //     logger.info("📆 Received request to schedule fetch for user {} at {}", request.getUserId(), request.getScheduledTime());
    //     scheduledUserFetchService.scheduleUserFetch(request.getUserId(), request.getScheduledTime());
    //     return ResponseEntity.ok("✅ User fetch scheduled for " + request.getScheduledTime());
    // }

    @PostMapping("/schedule-fetch")
    public ResponseEntity<String> scheduleUserFetch(@RequestBody UserFetchScheduleRequest request) {
        logger.info("📆 Received request to schedule fetch for user {} at {} in timezone {}", 
                request.getUserId(), request.getScheduledTime(), request.getTimezone());
        
        scheduledUserFetchService.scheduleUserFetch(
                request.getUserId(), 
                request.getScheduledTime(),
                request.getTimezone());
        
        return ResponseEntity.ok("✅ User fetch scheduled for " + request.getScheduledTime() + 
                " " + request.getTimezone());
    }

}