package com.example.demo.dto;

import java.util.List;

public class TimeSlotRequest {
    private Long userId;
    private int dayOfWeek;         // 0(일) ~ 6(토)
    private String slots;          // 길이 48의 "0101..." (옵션)
    private List<Integer> indices; // 선택 인덱스 0~47 (옵션)

    public TimeSlotRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getSlots() { return slots; }
    public void setSlots(String slots) { this.slots = slots; }

    public List<Integer> getIndices() { return indices; }
    public void setIndices(List<Integer> indices) { this.indices = indices; }
}
