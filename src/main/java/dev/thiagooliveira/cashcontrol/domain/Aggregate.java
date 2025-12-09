package dev.thiagooliveira.cashcontrol.domain;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Aggregate {

  private int version = 0;
  private final List<DomainEvent> pendingEvents = new ArrayList<>();

  public abstract UUID aggregateId();

  public abstract void whenTemplate(DomainEvent event);

  public List<DomainEvent> pendingEvents() {
    return List.copyOf(pendingEvents);
  }

  public void markEventsCommitted() {
    pendingEvents.clear();
  }

  public int getVersion() {
    return version;
  }

  protected void applyFromHistory(DomainEvent e) {
    when(e);
    version++;
  }

  protected void apply(DomainEvent event) {
    when(event);
    pendingEvents.add(event);
    version = event.version();
  }

  private void when(DomainEvent event) {
    whenTemplate(event);
  }
}
