package dev.thiagooliveira.cashcontrol.infrastructure.listener.transfer;

import dev.thiagooliveira.cashcontrol.application.transfer.TransferService;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.ConfirmTransferCommand;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.TransactionConfirmed;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.TransferConfirmed;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.TransferRequested;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transfer.TransferEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transfer.TransferJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

public class TransferEventListener {

  private final TransferService transferService;
  private final TransferJpaRepository transferJpaRepository;

  public TransferEventListener(
      TransferService transferService, TransferJpaRepository transferJpaRepository) {
    this.transferService = transferService;
    this.transferJpaRepository = transferJpaRepository;
  }

  @Order(1)
  @EventListener
  public void on(TransferRequested event) {
    this.transferJpaRepository.save(new TransferEntity(event));
  }

  @EventListener
  public void on(TransferConfirmed event) {
    var transfer =
        findById(event.id())
            .orElseThrow(() -> InfrastructureException.notFound("Transfer not found"));
    transfer.markAsConfirmed();
    this.transferJpaRepository.save(transfer);
  }

  @EventListener
  public void on(TransactionConfirmed event) {
    event
        .transferId()
        .flatMap(transferJpaRepository::findById)
        .ifPresent(
            transfer -> {
              var status = transfer.increaseStatus();
              transferJpaRepository.save(transfer);

              if (status.isConfirmed()) {
                transferService.confirm(
                    new ConfirmTransferCommand(transfer.getOrganizationId(), transfer.getId()));
              }
            });
  }

  private Optional<TransferEntity> findById(UUID id) {
    return this.transferJpaRepository.findById(id);
  }
}
