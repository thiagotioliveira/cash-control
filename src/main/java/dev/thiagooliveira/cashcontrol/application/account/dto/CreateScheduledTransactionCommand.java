package dev.thiagooliveira.cashcontrol.application.account.dto;

import dev.thiagooliveira.cashcontrol.shared.Recurrence;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record CreateScheduledTransactionCommand(
    UUID organizationId,
    UUID userId,
    UUID accountId,
    UUID categoryId,
    BigDecimal amount,
    LocalDate startDueDate,
    Recurrence recurrence,
    Optional<Integer> installments) {}
