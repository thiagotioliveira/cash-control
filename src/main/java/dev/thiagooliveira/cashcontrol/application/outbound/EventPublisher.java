package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;

public interface EventPublisher {

  void publishEvent(DomainEvent event);
}
