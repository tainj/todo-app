package me.tainj.todo.exception;

public class TelegramLinkException extends RuntimeException {
    public TelegramLinkException(String message) {
        super(message);
    }
}