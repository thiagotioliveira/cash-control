package dev.thiagooliveira.cashcontrol.application.transfer;

import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.ConfirmTransferCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.Transfer;

public class ConfirmTransfer {

  private final EventStore eventStore;
  private final EventPublisher publisher;

  public ConfirmTransfer(EventStore eventStore, EventPublisher publisher) {
    this.eventStore = eventStore;
    this.publisher = publisher;
  }

  public void execute(ConfirmTransferCommand command) {
    var pastEvents = eventStore.load(command.organizationId(), command.transferId());

    if (pastEvents.isEmpty()) {
      throw ApplicationException.notFound("transfer not found");
    }

    var transfer = Transfer.rehydrate(pastEvents);
    transfer.confirm();
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
