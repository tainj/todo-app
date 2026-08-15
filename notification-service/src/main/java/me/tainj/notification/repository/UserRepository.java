package me.tainj.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import me.tainj.notification.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}