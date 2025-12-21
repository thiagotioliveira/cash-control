package dev.thiagooliveira.cashcontrol.application.transfer;

import dev.thiagooliveira.cashcontrol.application.transfer.dto.ConfirmRevertTransferCommand;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.ConfirmTransferCommand;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.CreateTransferCommand;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.RevertTransferCommand;

public class TransferServiceImpl implements TransferService {
  private final CreateTransfer createTransfer;
  private final ConfirmTransfer confirmTransfer;
  private final RevertTransfer revertTransfer;
  private final ConfirmRevertTransfer confirmRevertTransfer;

  public TransferServiceImpl(
      CreateTransfer createTransfer,
      ConfirmTransfer confirmTransfer,
      RevertTransfer revertTransfer,
      ConfirmRevertTransfer confirmRevertTransfer) {
    this.createTransfer = createTransfer;
    this.confirmTransfer = confirmTransfer;
    this.revertTransfer = revertTransfer;
    this.confirmRevertTransfer = confirmRevertTransfer;
  }

  @Override
  public void create(CreateTransferCommand command) {
    this.createTransfer.execute(command);
  }

  @Override
  public void confirm(ConfirmTransferCommand command) {
    this.confirmTransfer.execute(command);
  }

  @Override
  public void revert(RevertTransferCommand command) {
    this.revertTransfer.execute(command);
  }

  @Override
  public void confirmRevert(ConfirmRevertTransferCommand command) {
    this.confirmRevertTransfer.execute(command);
  }
}
