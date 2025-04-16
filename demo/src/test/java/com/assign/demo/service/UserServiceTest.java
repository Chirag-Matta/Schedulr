package com.assign.demo.service;

import com.assign.demo.model.UserDetails;
import com.assign.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private UserService userService;

    private UserDetails testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserDetails(1L, "Test User", "test@example.com");
    }

    @Test
    void createUser_Success() {
        when(userRepository.save(any(UserDetails.class))).thenReturn(testUser);
        doNothing().when(kafkaProducerService).sendUserEvent(any(UserDetails.class));

        UserDetails result = userService.createUser(testUser);

        assertEquals(testUser, result);
        verify(userRepository).save(testUser);
        verify(kafkaProducerService).sendUserEvent(testUser);
    }

    @Test
    void getUserById_Found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(kafkaProducerService).sendUserEvent(any(UserDetails.class));

        UserDetails result = userService.getUserById(1L);

        assertEquals(testUser, result);
        verify(userRepository).findById(1L);
        verify(kafkaProducerService).sendUserEvent(testUser);
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        when(userRepository.findUserByIdNative(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(99L);
        });
    }

    @Test
    void getUserById_FoundWithNativeQuery() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findUserByIdNative(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(kafkaProducerService).sendUserEvent(any(UserDetails.class));

        UserDetails result = userService.getUserById(1L);

        assertEquals(testUser, result);
        verify(userRepository).findById(1L);
        verify(userRepository).findUserByIdNative(1L);
        verify(kafkaProducerService).sendUserEvent(testUser);
    }

    @Test
    void getAllUsers_Success() {
        List<UserDetails> users = Arrays.asList(
            new UserDetails(1L, "User1", "user1@example.com"),
            new UserDetails(2L, "User2", "user2@example.com")
        );
        
        when(userRepository.findAll()).thenReturn(users);

        List<UserDetails> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(users, result);
    }

    @Test
    void updateUser_Success() {
        UserDetails updatedUser = new UserDetails(1L, "Updated Name", "updated@example.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(UserDetails.class))).thenReturn(updatedUser);
        doNothing().when(kafkaProducerService).sendUserEvent(any(UserDetails.class));

        UserDetails result = userService.updateUser(1L, updatedUser);

        assertEquals("Updated Name", result.getName());
        assertEquals("updated@example.com", result.getEmail());
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(UserDetails.class));
        verify(kafkaProducerService).sendUserEvent(any(UserDetails.class));
    }

    @Test
    void updateUser_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(99L, testUser);
        });
    }

    @Test
    void deleteUser_Success() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }
}