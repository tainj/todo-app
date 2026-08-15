package me.tainj.todo.service;

import me.tainj.todo.dto.response.UserResponse;
import me.tainj.todo.exception.ErrorMessages;
import me.tainj.todo.exception.InvalidPasswordException;
import me.tainj.todo.exception.UserNotFoundException;
import me.tainj.todo.model.User;
import me.tainj.todo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(String username, String password, Boolean notifyTelegram, Boolean notifyWebsocket) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException(ErrorMessages.USERNAME_TAKEN);
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNotifyTelegram(notifyTelegram);
        user.setNotifyWebsocket(notifyWebsocket);
        userRepository.save(user);
        return user;
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidPasswordException(ErrorMessages.INVALID_PASSWORD);
        }
        return jwtService.generateToken(username);
    }

    public UserResponse updateSettings(String username, Boolean notifyTelegram, Boolean notifyWebsocket) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));
        user.setNotifyTelegram(notifyTelegram);
        user.setNotifyWebsocket(notifyWebsocket);
        return user.toResponse();
    }

    public UserResponse getMe(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));
        return user.toResponse();
    }
}
