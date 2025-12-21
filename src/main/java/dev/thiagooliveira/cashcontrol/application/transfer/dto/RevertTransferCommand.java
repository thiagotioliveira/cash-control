package dev.thiagooliveira.cashcontrol.application.transfer.dto;

import java.util.UUID;

public record RevertTransferCommand(UUID organizationId, UUID transferId, UUID userId) {}
