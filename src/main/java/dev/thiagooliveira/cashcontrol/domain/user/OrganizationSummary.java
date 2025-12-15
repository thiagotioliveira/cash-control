package dev.thiagooliveira.cashcontrol.domain.user;

import java.time.Instant;
import java.util.UUID;

public record OrganizationSummary(UUID id, String email, Instant createdAt, boolean active) {}
