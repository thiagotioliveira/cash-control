package dev.thiagooliveira.cashcontrol.application.transfer;

import dev.thiagooliveira.cashcontrol.application.transfer.dto.ConfirmRevertTransferCommand;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.ConfirmTransferCommand;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.CreateTransferCommand;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.RevertTransferCommand;

public interface TransferService {

  void create(CreateTransferCommand command);

  void confirm(ConfirmTransferCommand command);

  void revert(RevertTransferCommand command);

  void confirmRevert(ConfirmRevertTransferCommand command);
}
