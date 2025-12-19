package dev.thiagooliveira.cashcontrol.application.transaction.dto;

import dev.thiagooliveira.cashcontrol.shared.Recurrence;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record CreatePayableCommand(
    UUID organizationId,
    UUID userId,
    UUID accountId,
    UUID categoryId,
    BigDecimal amount,
    LocalDate startDueDate,
    Recurrence recurrence,
    Optional<Integer> installments) {}
