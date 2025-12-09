package dev.thiagooliveira.cashcontrol.infrastructure.config;

import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.infrastructure.event.EventPublisherAdapter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventPublisherConfig {

  @Bean
  EventPublisher eventPublisher(ApplicationEventPublisher publisher) {
    return new EventPublisherAdapter(publisher);
  }
}
