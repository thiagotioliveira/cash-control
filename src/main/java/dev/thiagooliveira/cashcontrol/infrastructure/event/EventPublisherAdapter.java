package dev.thiagooliveira.cashcontrol.infrastructure.event;

import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;

public class EventPublisherAdapter implements EventPublisher {

  private final ApplicationEventPublisher publisher;

  public EventPublisherAdapter(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  @Override
  public void publishEvent(DomainEvent event) {
    this.publisher.publishEvent(event);
  }
}
