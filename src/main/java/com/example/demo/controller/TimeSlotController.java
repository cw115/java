package com.example.demo.controller;

import com.example.demo.Repository.TimeSlotRepository;
import com.example.demo.dto.TimeSlotRequest;
import com.example.demo.entity.TimeSlot;
import com.example.demo.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/time-slots")
public class TimeSlotController {

    private final TimeSlotService timeSlotService;
    private final TimeSlotRepository timeSlotRepository;

    /** 저장/갱신 */
    @PostMapping
    public Map<String, Object> save(@RequestBody TimeSlotRequest req) {
        timeSlotService.upsert(req);
        return Map.of("ok", true);
    }

    /** 개별 조회: bitstring -> indices */
    @GetMapping
    public Map<String, Object> get(@RequestParam Long userId,
                                   @RequestParam int dayOfWeek) {
        Optional<TimeSlot> opt = timeSlotRepository.findByUser_UserIdAndDayOfWeek(userId, dayOfWeek);
        if (opt.isEmpty()) return Map.of("indices", List.of(), "slots", "0".repeat(48));

        String s = opt.get().getSlots();
        List<Integer> idx = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) if (s.charAt(i) == '1') idx.add(i);
        return Map.of("indices", idx, "slots", s);
    }
}
