package dev.thiagooliveira.cashcontrol.infrastructure.listener.transfer;

import dev.thiagooliveira.cashcontrol.application.transaction.TransactionService;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.RevertTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.transfer.TransferService;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.ConfirmRevertTransferCommand;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.ConfirmTransferCommand;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.*;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transfer.TransferEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transfer.TransferJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

public class TransferEventListener {

  private final TransferService transferService;
  private final TransactionService transactionService;
  private final TransferJpaRepository transferJpaRepository;
  private final TransactionJpaRepository transactionJpaRepository;

  public TransferEventListener(
      TransferService transferService,
      TransactionService transactionService,
      TransferJpaRepository transferJpaRepository,
      TransactionJpaRepository transactionJpaRepository) {
    this.transferService = transferService;
    this.transactionService = transactionService;
    this.transferJpaRepository = transferJpaRepository;
    this.transactionJpaRepository = transactionJpaRepository;
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

  @EventListener
  public void on(RevertTransferRequested event) {
    var transactions =
        this.transactionJpaRepository
            .findByOrganizationIdAndTransferId(event.organizationId(), event.transferId())
            .stream()
            .toList();

    var credit =
        transactions.stream().filter(t -> t.getType().isCredit()).findFirst().orElseThrow();
    this.transactionService.revert(
        new RevertTransactionCommand(
            event.organizationId(), credit.getAccount().getId(), credit.getId(), event.userId()));
    var debit = transactions.stream().filter(t -> t.getType().isDebit()).findFirst().orElseThrow();
    this.transactionService.revert(
        new RevertTransactionCommand(
            event.organizationId(), debit.getAccount().getId(), debit.getId(), event.userId()));
    this.transferService.confirmRevert(
        new ConfirmRevertTransferCommand(
            event.organizationId(), event.transferId(), event.userId()));
  }

  @EventListener
  public void on(TransferReverted event) {
    this.transferJpaRepository.deleteById(event.transferId());
  }

  private Optional<TransferEntity> findById(UUID id) {
    return this.transferJpaRepository.findById(id);
  }
}
