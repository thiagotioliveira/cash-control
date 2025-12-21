package dev.thiagooliveira.cashcontrol.application.transfer.dto;

import java.util.UUID;

public record ConfirmRevertTransferCommand(UUID organizationId, UUID transferId, UUID userId) {}
