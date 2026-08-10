package me.tainj.todo.controller;


import me.tainj.todo.dto.response.TelegramLinkResponse;
import me.tainj.todo.service.TelegramLinkService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/telegram")
public class TelegramController {
    private final TelegramLinkService telegramLinkService;

    public TelegramController(TelegramLinkService telegramLinkService) {
        this.telegramLinkService = telegramLinkService;
    }

    @GetMapping
    public TelegramLinkResponse createLink(Authentication authentication) {
        return telegramLinkService.createLink(getCurrentUsername(authentication));
    }

    private String getCurrentUsername(Authentication authentication) {
        return authentication.getName();
    }
}
