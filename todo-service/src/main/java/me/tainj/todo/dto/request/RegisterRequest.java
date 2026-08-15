package me.tainj.todo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterRequest(
        String username,
        String password,
        @JsonProperty("notify_telegram") boolean notifyTelegram,
        @JsonProperty("notify_websocket") boolean notifyWebsocket
) {
}