// src/main/java/com/example/demo/controller/MeetingTimeController.java
package com.example.demo.controller;

import com.example.demo.service.MeetingTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MeetingTimeController {

    private final MeetingTimeService meetingTimeService;

    @GetMapping("/available-time")
    public List<String> available(@RequestParam Long projectId) {
        return meetingTimeService.calculateAvailableTime(projectId);
    }
}
