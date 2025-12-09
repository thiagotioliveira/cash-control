package dev.thiagooliveira.cashcontrol.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.eventstore.EventJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.eventstore.EventStoreAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventStoreConfig {

  @Bean
  EventStore eventStore(EventJpaRepository repository, ObjectMapper objectMapper) {
    return new EventStoreAdapter(repository, objectMapper);
  }
}
