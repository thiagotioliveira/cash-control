package dev.thiagooliveira.cashcontrol.infrastructure.transactional.transfer;

import dev.thiagooliveira.cashcontrol.application.transfer.TransferService;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.ConfirmRevertTransferCommand;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.ConfirmTransferCommand;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.CreateTransferCommand;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.RevertTransferCommand;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TransferServiceProxy implements TransferService {

  private final TransferService transferService;

  public TransferServiceProxy(TransferService transferService) {
    this.transferService = transferService;
  }

  @Override
  public void create(CreateTransferCommand command) {
    this.transferService.create(command);
  }

  @Override
  public void confirm(ConfirmTransferCommand command) {
    this.transferService.confirm(command);
  }

  @Override
  public void revert(RevertTransferCommand command) {
    this.transferService.revert(command);
  }

  @Override
  public void confirmRevert(ConfirmRevertTransferCommand command) {
    this.transferService.confirmRevert(command);
  }
}
