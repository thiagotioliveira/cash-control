package dev.thiagooliveira.cashcontrol.application.category.dto;

import dev.thiagooliveira.cashcontrol.shared.CategoryType;
import java.util.UUID;

public record CreateCategoryCommand(
    UUID organizationId, UUID userId, String name, String hashColor, CategoryType type) {}
