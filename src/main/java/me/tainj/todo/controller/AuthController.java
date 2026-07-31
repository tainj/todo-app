package me.tainj.todo.controller;

import me.tainj.todo.dto.request.AuthRequest;
import me.tainj.todo.dto.request.RegisterRequest;
import me.tainj.todo.dto.response.AuthResponse;
import me.tainj.todo.dto.response.UserResponse;
import me.tainj.todo.model.User;
import me.tainj.todo.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final  UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserResponse register(@RequestBody RegisterRequest request) {
        User user = userService.register(
                request.username(),
                request.password()
        );

        return new UserResponse(
                user.getId(),
                user.getUsername()
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return new AuthResponse(
                userService.login(
                        request.getUsername(),
                        request.getPassword()
                )
        );
    }

}
