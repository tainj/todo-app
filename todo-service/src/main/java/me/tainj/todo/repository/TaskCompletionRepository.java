package me.tainj.todo.repository;

import me.tainj.todo.model.TaskCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface TaskCompletionRepository extends JpaRepository<TaskCompletion, Long> {
    @Query("SELECT tc FROM TaskCompletion tc JOIN FETCH tc.task t WHERE t.user.username = :username ORDER BY tc.completedAt DESC")
    List<TaskCompletion> findByUsername(@Param("username") String username);
}
