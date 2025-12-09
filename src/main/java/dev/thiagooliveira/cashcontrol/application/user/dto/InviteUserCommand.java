package dev.thiagooliveira.cashcontrol.application.user.dto;

import java.util.UUID;

public record InviteUserCommand(String name, String email, UUID organizationId) {}
