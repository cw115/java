package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "time_slots")
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "time_slot_id")
    private Long timeSlotId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;                 // 이미 있는 User 엔티티 매핑

    private int dayOfWeek;             // 0(일) ~ 6(토)

    // 48자리 문자열 (30분 × 24시간)
    // 예: "000000111111000000...": '1'은 가능, '0'은 불가
    @Column(length = 48, nullable = false)
    private String slots;
}
