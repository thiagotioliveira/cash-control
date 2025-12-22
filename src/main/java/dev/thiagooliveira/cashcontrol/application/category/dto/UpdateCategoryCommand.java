package dev.thiagooliveira.cashcontrol.application.category.dto;

import java.util.UUID;

public record UpdateCategoryCommand(
    UUID organizationId, UUID userId, UUID categoryId, String name, String hashColor) {}
