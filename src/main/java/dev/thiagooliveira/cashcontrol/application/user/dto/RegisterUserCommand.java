package dev.thiagooliveira.cashcontrol.application.user.dto;

public record RegisterUserCommand(
    String name, String email, String password, String passwordConfirmation) {}
