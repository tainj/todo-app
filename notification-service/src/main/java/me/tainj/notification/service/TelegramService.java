package me.tainj.notification.service;

import me.tainj.notification.model.NotificationEvent;
import me.tainj.notification.model.User;
import me.tainj.notification.repository.UserRepository;
import me.tainj.notification.util.TelegramMessages;
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
        log.info("=== INCOMING UPDATE ===");
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            log.info("Update does not contain a text message");
            return;
        }

        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        String username = update.getMessage().getFrom().getUserName();

        log.info("Message from @{} (chatId={}): '{}'", username, chatId, text);

        if (text.startsWith("/start ")) {
            String token = text.substring(7);
            String userId = redisTemplate.opsForValue().get("telegram:link:" + token);

            if (userId != null) {
                User user = userRepository.findById(Long.parseLong(userId)).orElseThrow();
                user.setTelegramChatId(chatId);
                userRepository.save(user);
                redisTemplate.delete("telegram:link:" + token);

                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(TelegramMessages.WELCOME);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    log.error("Failed to send welcome message: {}", e.getMessage());
                }
            }
        }
    }

    public void send(NotificationEvent event) {
        String key = "notification:sent:" + event.getTaskId() + ":" + event.getMinuteBefore();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) return;

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(event.getChatId()));
        message.setText(TelegramMessages.createMessage(event));
        message.setParseMode("Markdown");

        try {
            execute(message);
            redisTemplate.opsForValue().set(key, "sent", Duration.ofHours(24));
            log.info("Telegram notification sent for task {}", event.getTaskId());
        } catch (TelegramApiException e) {
            log.error("Failed to send telegram message: {}", e.getMessage());
        }
    }
}