package me.tainj.notification.service;

import Te

@Service
public class TelegramService extends TelegramLongPollingBot {

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

//    @Override
//    public void onUpdateReceived(Update update) {
//        // обработка входящих сообщений от юзера
//    }

    public void send(NotificationEvent event) {
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
}