package dev.thiagooliveira.cashcontrol.application.transfer.dto;

import java.util.UUID;

public record ConfirmTransferCommand(UUID organizationId, UUID userId, UUID transferId) {}
