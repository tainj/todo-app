package me.tainj.notification.config;

import me.tainj.notification.service.TelegramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import jakarta.annotation.PostConstruct;

@Configuration
public class BotRegistrar {

    private static final Logger log = LoggerFactory.getLogger(BotRegistrar.class);
    private final TelegramService telegramService;

    public BotRegistrar(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramService);
            log.info("✅ Бот успешно зарегистрирован в Telegram!");
        } catch (TelegramApiException e) {
            log.error("❌ Ошибка регистрации бота: {}", e.getMessage(), e);
        }
    }
}