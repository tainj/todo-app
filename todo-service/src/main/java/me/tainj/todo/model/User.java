package me.tainj.todo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.tainj.todo.dto.response.UserResponse;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    @Column(name = "telegram_chat_id")
    private Long telegramChatId;

    @Column(name = "notify_telegram")
    private boolean notifyTelegram = false;

    @Column(name = "notify_websocket")
    private boolean notifyWebsocket = true;

    public UserResponse toResponse() {
        return new UserResponse(id, username, notifyTelegram, notifyWebsocket);
    }
}
