package me.tainj.todo.service;

import me.tainj.todo.dto.response.TelegramLinkResponse;
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

        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("user not found"));
        String userId = String.valueOf(user.getId());

        try {
            redisTemplate.opsForValue().set(key, userId, Duration.ofMinutes(10));

        } catch (Exception e) {
            log.error("failed to write key in redis: {}", e.getMessage());
        }
        return new TelegramLinkResponse(url);
    }
}
