package dev.thiagooliveira.cashcontrol.application.transfer;

import dev.thiagooliveira.cashcontrol.application.transfer.dto.ConfirmTransferCommand;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.CreateTransferCommand;

public class TransferServiceImpl implements TransferService {
  private final CreateTransfer createTransfer;
  private final ConfirmTransfer confirmTransfer;

  public TransferServiceImpl(CreateTransfer createTransfer, ConfirmTransfer confirmTransfer) {
    this.createTransfer = createTransfer;
    this.confirmTransfer = confirmTransfer;
  }

  @Override
  public void create(CreateTransferCommand command) {
    this.createTransfer.execute(command);
  }

  @Override
  public void confirm(ConfirmTransferCommand command) {
    this.confirmTransfer.execute(command);
  }
}
