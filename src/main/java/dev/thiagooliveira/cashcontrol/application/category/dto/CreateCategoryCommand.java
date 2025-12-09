package dev.thiagooliveira.cashcontrol.application.category.dto;

import dev.thiagooliveira.cashcontrol.shared.TransactionType;

public record CreateCategoryCommand(String name, String hashColor, TransactionType type) {}
