package dev.thiagooliveira.cashcontrol.application.transaction.dto;

import java.util.UUID;

public record GetTransactionCommand(UUID organizationId, UUID accountId, UUID transactionId) {}
