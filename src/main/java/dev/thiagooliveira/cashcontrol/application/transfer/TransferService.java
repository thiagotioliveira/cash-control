package dev.thiagooliveira.cashcontrol.application.transfer;

import dev.thiagooliveira.cashcontrol.application.transfer.dto.ConfirmTransferCommand;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.CreateTransferCommand;

public interface TransferService {

  void create(CreateTransferCommand command);

  void confirm(ConfirmTransferCommand command);
}
