package com.assign.demo.controller;

import com.assign.demo.model.UserDetails;
import com.assign.demo.service.KafkaProducerService;
import com.assign.demo.service.QuartzSchedulerService;
import com.assign.demo.service.UserNotFoundException;
import com.assign.demo.service.UserService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @MockBean
    private QuartzSchedulerService quartzSchedulerService;

    @Test
    public void testGetUserById_success() throws Exception {
        UserDetails user = new UserDetails(1L, "John Doe", "john@example.com");

        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user_details/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    public void testGetUserById_notFound() throws Exception {
        when(userService.getUserById(2L)).thenThrow(new UserNotFoundException("Not Found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user_details/2"))
            .andExpect(status().isNotFound());
    }
}
