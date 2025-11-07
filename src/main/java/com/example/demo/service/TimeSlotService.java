package com.example.demo.service;

import com.example.demo.Repository.TimeSlotRepository;
import com.example.demo.dto.TimeSlotRequest;
import com.example.demo.entity.TimeSlot;
import com.example.demo.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;

    @PersistenceContext
    private EntityManager em;

    /** 기존 방식: 바로 slots(48자리 bitstring) 저장/갱신 */
    @Transactional
    public void upsertTimeSlot(Long userId, int dayOfWeek, String slots) {
        // FK 제약: users(user_id) 반드시 존재해야 함
        User userRef = em.getReference(User.class, userId);

        Optional<TimeSlot> opt = timeSlotRepository
                .findByUser_UserIdAndDayOfWeek(userId, dayOfWeek);

        TimeSlot ts = opt.orElseGet(() -> {
            TimeSlot t = new TimeSlot();
            t.setUser(userRef);
            t.setDayOfWeek(dayOfWeek);
            return t;
        });

        ts.setSlots(slots);
        timeSlotRepository.save(ts);
    }

    /** DTO 기반 저장/갱신: indices -> 48자리 bitstring 변환 후 저장 */
    @Transactional
    public void upsert(TimeSlotRequest req) {
        String slotsStr = req.getSlots();
        if (slotsStr == null || slotsStr.length() != 48) {
            char[] arr = new char[48];
            Arrays.fill(arr, '0');
            if (req.getIndices() != null) {
                for (Integer i : req.getIndices()) {
                    if (i != null && i >= 0 && i < 48) arr[i] = '1';
                }
            }
            slotsStr = new String(arr);
        }
        upsertTimeSlot(req.getUserId(), req.getDayOfWeek(), slotsStr);
    }

    /** 프로젝트의 요일별(0~6) 공통 가능시간 교집합을 48자리 bitstring으로 반환 */
    @Transactional(readOnly = true)
    public List<String> intersectByProject(Long projectId) {
        List<String> out = new ArrayList<>();
        for (int d = 0; d <= 6; d++) {
            List<String> slotsList = timeSlotRepository.findSlotsByProjectAndDay(projectId, d);
            if (slotsList.isEmpty()) {
                out.add("Day " + d + ": " + "0".repeat(48));
                continue;
            }
            char[] acc = new char[48];
            Arrays.fill(acc, '1'); // AND 누적
            for (String s : slotsList) {
                for (int i = 0; i < 48; i++) {
                    if (i >= s.length() || s.charAt(i) != '1') acc[i] = '0';
                }
            }
            out.add("Day " + d + ": " + new String(acc));
        }
        return out;
    }
}
