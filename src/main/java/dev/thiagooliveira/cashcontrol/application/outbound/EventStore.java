package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import java.util.List;
import java.util.UUID;

public interface EventStore {
  void append(
      UUID organizationId,
      UUID userId,
      UUID aggregateId,
      List<DomainEvent> events,
      int expectedVersion);

  List<DomainEvent> load(UUID organizationId, UUID aggregateId);
}
