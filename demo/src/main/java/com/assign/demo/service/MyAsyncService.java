package com.assign.demo.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MyAsyncService {

    @Async("taskExecutor")
    public void runInBackground() {
        // Your logic here
        System.out.println("Running async task on thread: " + Thread.currentThread().getName());
    }
}
