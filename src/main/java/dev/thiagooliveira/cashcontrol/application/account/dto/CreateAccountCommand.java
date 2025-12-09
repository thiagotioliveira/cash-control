package dev.thiagooliveira.cashcontrol.application.account.dto;

import java.util.UUID;

public record CreateAccountCommand(UUID organizationId, UUID bankId, String name) {}
