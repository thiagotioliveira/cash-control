package dev.thiagooliveira.cashcontrol.infrastructure.persistence.eventstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventStoreAdapter implements EventStore {

  private static final String PREFIX_EVENT_TYPE = "dev.thiagooliveira.cashcontrol.domain.event.";

  private final EventJpaRepository repository;
  private final ObjectMapper objectMapper;

  public EventStoreAdapter(EventJpaRepository repository, ObjectMapper objectMapper) {
    this.repository = repository;
    this.objectMapper = objectMapper;
  }

  @Override
  public void append(UUID aggregateId, List<DomainEvent> events, int expectedVersion) {
    List<EventEntity> current = repository.findByAggregateIdOrderByVersion(aggregateId);

    if (current.size() != expectedVersion) {
      throw InfrastructureException.conflict("concurrency error: version mismatch");
    }

    int version = expectedVersion;
    for (DomainEvent event : events) {
      try {
        EventEntity entity = new EventEntity();
        entity.setAggregateId(aggregateId);
        entity.setVersion(++version);
        entity.setEventType(
            event.getClass().getCanonicalName().substring(PREFIX_EVENT_TYPE.length()));
        entity.setPayload(objectMapper.writeValueAsString(event));
        entity.setOccurredAt(event.occurredAt());

        repository.save(entity);
      } catch (Exception e) {
        throw InfrastructureException.internalError("error serializing event", e);
      }
    }
  }

  @Override
  public List<DomainEvent> load(UUID aggregateId) {
    var entities = repository.findByAggregateIdOrderByVersion(aggregateId);
    return convertToDomainEvents(entities);
  }

  private List<DomainEvent> convertToDomainEvents(List<EventEntity> entities) {
    List<DomainEvent> events = new ArrayList<>();
    for (EventEntity entity : entities) {
      try {
        Class<?> clazz = Class.forName(PREFIX_EVENT_TYPE.concat(entity.getEventType()));
        DomainEvent event = (DomainEvent) objectMapper.readValue(entity.getPayload(), clazz);
        events.add(event);
      } catch (Exception e) {
        throw InfrastructureException.internalError("error deserializing event", e);
      }
    }
    return events;
  }
}
