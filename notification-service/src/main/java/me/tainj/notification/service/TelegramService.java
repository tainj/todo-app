package me.tainj.notification.service;

import me.tainj.notification.model.NotificationEvent;
import me.tainj.notification.model.User;
import me.tainj.notification.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.time.Duration;

@Service
public class TelegramService extends TelegramLongPollingBot {
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final Logger log = LoggerFactory.getLogger(TelegramService.class);

    public TelegramService(RedisTemplate<String, String> redisTemplate, UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
    }

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${telegram.bot.username}")
    private String username;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        if (text.startsWith("/start ")) {
            String token = text.substring(7);
            String userId = redisTemplate.opsForValue().get("telegram:link:" + token);
            if (userId != null) {
                User user = userRepository.findById(Long.parseLong(userId)).orElseThrow();
                user.setTelegramChatId(chatId);
                userRepository.save(user);
                redisTemplate.delete("telegram:link:" + token);
            }
        }
    }

    public void send(NotificationEvent event) {
        SendMessage message = new SendMessage();
        message.setChatId(event.getChatId());
        message.setText(event.getMessage());

        String key = "notification:sent:" + event.getTaskId();
        if (redisTemplate.hasKey(key)) {
            return;
        }

        try {
            execute(message);
            redisTemplate.opsForValue().set(key, "sent", Duration.ofHours(24));
        } catch (TelegramApiException e) {
            log.error("Failed to send telegram message: {}", e.getMessage());
        }
    }
}