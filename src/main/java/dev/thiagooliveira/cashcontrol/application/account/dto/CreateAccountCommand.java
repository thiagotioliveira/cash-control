package dev.thiagooliveira.cashcontrol.application.account.dto;

import java.util.UUID;

public record CreateAccountCommand(UUID bankId, String name) {}
