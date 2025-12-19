package dev.thiagooliveira.cashcontrol.application.transaction.dto;

import java.util.UUID;

public record RevertTransactionCommand(
    UUID organizationId, UUID accountId, UUID transactionId, UUID userId) {}
