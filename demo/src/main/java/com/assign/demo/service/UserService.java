package com.assign.demo.service;

import com.assign.demo.model.UserDetails;
import com.assign.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    public UserDetails createUser(UserDetails user) {
        log.info("📝 Creating user: {}", user);
        UserDetails savedUser = userRepository.save(user);
        log.info("✅ User saved: {}", savedUser);
        kafkaProducerService.sendUserEvent(savedUser); // send to Kafka
        log.info("📤 User event sent to Kafka: {}", savedUser);
        return savedUser;
    }

    public Optional<UserDetails> getUser(Long id) {
        log.info("🔍 Fetching user with ID: {}", id);
        // Try the standard method first
        Optional<UserDetails> user = userRepository.findById(id);
        
        // If not found, try the native query as a fallback
        if (user.isEmpty()) {
            log.info("🔍 User not found with standard query, trying native query for ID: {}", id);
            user = userRepository.findUserByIdNative(id);
        }
        
        return user;
    }

    public List<UserDetails> getAllUsers() {
        log.info("📦 Fetching all users");
        return userRepository.findAll();
    }

    public UserDetails updateUser(Long id, UserDetails updatedUser) {
        log.info("✏ Updating user with ID: {}", id);
        return userRepository.findById(id)
            .map(existing -> {
                existing.setName(updatedUser.getName());
                existing.setEmail(updatedUser.getEmail());
                UserDetails saved = userRepository.save(existing);
                log.info("✅ User updated: {}", saved);
                kafkaProducerService.sendUserEvent(saved); // send to Kafka
                log.info("📤 Updated user event sent to Kafka: {}", saved);
                return saved;
            })
            .orElseThrow(() -> {
                log.warn("❌ User not found with ID: {}", id);
                return new UserNotFoundException("User not found with ID: " + id);
            });
    }

    public void deleteUser(Long id) {
        log.info("🗑 Deleting user with ID: {}", id);
        userRepository.deleteById(id);
    }

    // public UserDetails getUserById(Long id) {
    //     return getUser(id)
    //         .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        
    // }

    public UserDetails getUserById(Long id) {
        UserDetails user = getUser(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        
        log.info("📤 Sending user fetch event to Kafka: {}", user);
        kafkaProducerService.sendUserEvent(user);
    
        return user;
    }
}