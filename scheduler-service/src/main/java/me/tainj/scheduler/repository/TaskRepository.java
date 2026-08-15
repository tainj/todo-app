package me.tainj.scheduler.repository;

import me.tainj.scheduler.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("""
    SELECT t FROM Task t
    WHERE t.deadline IS NOT NULL
    AND t.completed = false
    AND t.user.telegramChatId IS NOT NULL
    AND t.deadline BETWEEN :from AND :to
    """)
    List<Task> findTasksWithDeadlineBetween(
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );
}