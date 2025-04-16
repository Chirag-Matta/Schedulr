package com.assign.demo.quartz;

import com.assign.demo.service.UserNotFoundException;
import com.assign.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserFetchJobTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserFetchJob userFetchJob;

    private JobExecutionContext jobContext;
    private JobDataMap jobDataMap;

    @BeforeEach
    void setUp() {
        jobContext = mock(JobExecutionContext.class);
        jobDataMap = new JobDataMap();
        jobDataMap.put("userId", 1L);
        
        when(jobContext.getMergedJobDataMap()).thenReturn(jobDataMap);
    }

    @Test
    void execute_Success() throws JobExecutionException {
        doNothing().when(userService).getUserById(1L);

        userFetchJob.execute(jobContext);

        verify(userService).getUserById(1L);
    }

    @Test
    void execute_UserNotFound() {
        doThrow(new UserNotFoundException("User not found")).when(userService).getUserById(1L);

        assertThrows(JobExecutionException.class, () -> {
            userFetchJob.execute(jobContext);
        });

        verify(userService).getUserById(1L);
    }
}