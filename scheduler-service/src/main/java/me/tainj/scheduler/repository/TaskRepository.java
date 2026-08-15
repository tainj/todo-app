package me.tainj.scheduler.repository;

import me.tainj.scheduler.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE t.createdAt <= :now AND t.user.telegramChatId IS NOT NULL")
    List<Task> findOverdueTasks(@Param("now") OffsetDateTime now);
}