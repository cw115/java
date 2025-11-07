package com.example.demo.service;

import com.example.demo.Repository.TimeSlotRepository;
import com.example.demo.Repository.UserProjectRepository;
import com.example.demo.entity.TimeSlot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MeetingTimeService {

    private final UserProjectRepository userProjectRepository;
    private final TimeSlotRepository timeSlotRepository;

    public List<String> calculateAvailableTime(Long projectId) {

        List<Long> userIds = userProjectRepository.findUserIdsByProjectId(projectId);
        if (userIds == null || userIds.isEmpty()) {
            return List.of(); // 프로젝트에 멤버 없음
        }

        List<TimeSlot> slots = timeSlotRepository.findByUser_UserIdIn(userIds);
        if (slots == null || slots.isEmpty()) {
            return List.of(); // 등록된 가용시간 없음
        }

        // dayOfWeek별로 slots 모으기
        Map<Integer, List<String>> byDay = new HashMap<>();
        for (TimeSlot ts : slots) {
            byDay.computeIfAbsent(ts.getDayOfWeek(), k -> new ArrayList<>())
                    .add(normalize(ts.getSlots())); // <- 여기서 48자리로 정규화
        }

        List<String> result = new ArrayList<>();
        for (int day = 0; day < 7; day++) {
            List<String> list = byDay.get(day);
            if (list == null || list.isEmpty()) continue;

            String combined = list.get(0);
            for (int i = 1; i < list.size(); i++) {
                combined = intersect48(combined, list.get(i));
            }
            result.add("Day " + day + ": " + combined); // 프론트가 이 포맷을 파싱함
        }
        return result;
    }

    // slots를 **정확히 48자리 '0'/'1'**로 맞춤
    private String normalize(String s) {
        if (s == null) s = "";
        s = s.replaceAll("[^01]", "");   // 혹시 공백/기타문자 있으면 제거
        if (s.length() > 48) {
            s = s.substring(0, 48);
        } else if (s.length() < 48) {
            StringBuilder sb = new StringBuilder(48);
            sb.append(s);
            while (sb.length() < 48) sb.append('0');
            s = sb.toString();
        }
        return s;
    }

    // 48칸 교집합(AND)
    private String intersect48(String a, String b) {
        // 방어적으로도 48로 맞춤
        a = normalize(a);
        b = normalize(b);
        StringBuilder sb = new StringBuilder(48);
        for (int i = 0; i < 48; i++) {
            sb.append((a.charAt(i) == '1' && b.charAt(i) == '1') ? '1' : '0');
        }
        return sb.toString();
    }
}
