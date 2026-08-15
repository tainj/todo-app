package me.tainj.todo.service;

import me.tainj.todo.dto.response.TelegramLinkResponse;
import me.tainj.todo.exception.ErrorMessages;
import me.tainj.todo.exception.UserNotFoundException;
import me.tainj.todo.model.User;
import me.tainj.todo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class TelegramLinkService {
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final Logger log = LoggerFactory.getLogger(TelegramLinkService.class);

    @Value("${telegram.bot.username}")
    private String botUsername;

    public TelegramLinkService(UserRepository userRepository, RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    public TelegramLinkResponse createLink(String username) {
        String token = UUID.randomUUID().toString();
        String url = "https://t.me/" + botUsername + "?start=" + token;
        String key = "telegram:link:" + token;

        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));
        String userId = String.valueOf(user.getId());

        try {
            redisTemplate.opsForValue().set(key, userId, Duration.ofMinutes(10));

        } catch (Exception e) {
            log.error("Failed to write key in Redis for token {} (userId={})", token, userId, e);
            throw new me.tainj.todo.exception.TelegramLinkException("Telegram link is temporarily unavailable");
        }
        return new TelegramLinkResponse(url);
    }
}
