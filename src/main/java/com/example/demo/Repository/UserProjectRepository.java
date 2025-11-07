// src/main/java/com/example/demo/Repository/UserProjectRepository.java
package com.example.demo.Repository;

import com.example.demo.entity.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserProjectRepository extends JpaRepository<UserProject, Long> {

    // ✅ UserProject 엔티티에 Long userId, Long projectId 필드가 있는 경우
    @Query("SELECT up.userId FROM UserProject up WHERE up.projectId = :projectId")
    List<Long> findUserIdsByProjectId(Long projectId);
}
