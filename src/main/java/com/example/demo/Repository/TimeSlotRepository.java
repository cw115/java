// src/main/java/com/example/demo/Repository/TimeSlotRepository.java
package com.example.demo.Repository;

import com.example.demo.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    Optional<TimeSlot> findByUser_UserIdAndDayOfWeek(Long userId, int dayOfWeek);
    List<TimeSlot> findByUser_UserIdIn(List<Long> userIds);

    // ✅ 프로젝트 + 요일의 slots만 모아서 리턴 (네이티브: 테이블/컬럼명 DB 그대로)
    @Query(
            value = """
            SELECT ts.slots
            FROM time_slots ts
            JOIN user_projects up ON up.user_id = ts.user_id
            WHERE up.project_id = :projectId
              AND ts.day_of_week = :dayOfWeek
        """,
            nativeQuery = true
    )
    List<String> findSlotsByProjectAndDay(Long projectId, int dayOfWeek);
}
