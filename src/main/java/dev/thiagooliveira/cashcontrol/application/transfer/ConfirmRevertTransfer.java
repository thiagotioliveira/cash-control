package dev.thiagooliveira.cashcontrol.application.transfer;

import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.ConfirmRevertTransferCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.Transfer;

public class ConfirmRevertTransfer {

  private final EventStore eventStore;
  private final EventPublisher publisher;

  public ConfirmRevertTransfer(EventStore eventStore, EventPublisher publisher) {
    this.eventStore = eventStore;
    this.publisher = publisher;
  }

  public void execute(ConfirmRevertTransferCommand command) {
    var pastEvents = eventStore.load(command.organizationId(), command.transferId());

    if (pastEvents.isEmpty()) {
      throw ApplicationException.notFound("transfer not found");
    }

    var transfer = Transfer.rehydrate(pastEvents);
    transfer.confirmRevert(command.userId());
    var newEvents = transfer.pendingEvents();
    eventStore.append(
        command.organizationId(),
        transfer.getId(),
        newEvents,
        transfer.getVersion() - newEvents.size());
    newEvents.forEach(publisher::publishEvent);

    transfer.markEventsCommitted();
  }
}
