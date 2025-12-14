package dev.thiagooliveira.cashcontrol.application.user.dto;

import java.time.Instant;
import java.util.UUID;

public record GetOrganizationItem(UUID id, String email, Instant createdAt, boolean active) {}
