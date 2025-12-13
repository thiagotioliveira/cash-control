package dev.thiagooliveira.cashcontrol.application.category.dto;

import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.util.UUID;

public record GetCategoryItem(UUID id, String name, String hashColor, TransactionType type) {}
