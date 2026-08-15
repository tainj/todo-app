package me.tainj.todo.service;

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

    public User register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("username already taken");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    public String login(String username, String password) {
        User user = this.userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("user not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidPasswordException("invalid password");
        }
        return jwtService.generateToken(username);
    }
}
