package dev.thiagooliveira.cashcontrol.domain.transaction;

import dev.thiagooliveira.cashcontrol.shared.Recurrence;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionTemplateSummary(
    UUID id,
    UUID organizationId,
    UUID accountId,
    UUID categoryId,
    TransactionType type,
    String description,
    BigDecimal amount,
    Recurrence recurrence,
    LocalDate startDate,
    Integer totalInstallments) {}
