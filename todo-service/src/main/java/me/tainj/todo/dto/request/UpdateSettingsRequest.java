package me.tainj.todo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateSettingsRequest(
        @JsonProperty("notify_telegram") Boolean notifyTelegram,
        @JsonProperty("notify_websocket") Boolean notifyWebsocket
) {}