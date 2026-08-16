package me.tainj.todo.controller;

import me.tainj.todo.dto.request.UpdateSettingsRequest;
import me.tainj.todo.dto.response.UserResponse;
import me.tainj.todo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/settings")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateSettings(@RequestBody UpdateSettingsRequest request, Authentication authentication) {
        return userService.updateSettings(
                getCurrentUsername(authentication),
                request.notifyTelegram(),
                request.notifyWebsocket()
        );
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse me(Authentication authentication) {
        return userService.getMe(authentication.getName());
    }

    private String getCurrentUsername(Authentication authentication)     {
        return authentication.getName();
    }
}
