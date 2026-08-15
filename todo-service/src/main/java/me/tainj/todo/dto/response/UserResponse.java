package me.tainj.todo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({"id", "username", "notify_telegram", "notify_websocket"})
public record UserResponse(
        Long id,
        String username,
        @JsonProperty("notify_telegram") boolean notifyTelegram,
        @JsonProperty("notify_websocket") boolean notifyWebsocket
) {
}