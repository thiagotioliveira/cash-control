package dev.thiagooliveira.cashcontrol.application.transaction.dto;

import java.time.LocalDate;
import java.util.UUID;

public record GetTransactionsCommand(UUID accountId, LocalDate startDate, LocalDate endDate) {}
