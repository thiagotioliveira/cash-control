package dev.thiagooliveira.cashcontrol.domain.user;

import dev.thiagooliveira.cashcontrol.domain.Aggregate;
import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.domain.event.user.v1.OrganizationCreated;
import dev.thiagooliveira.cashcontrol.domain.exception.DomainException;
import java.time.Instant;
import java.util.UUID;

public class Organization extends Aggregate {

  private UUID id;
  private String email;
  private Instant createdAt;
  private boolean active;

  private Organization(UUID id, String email, Instant createdAt, boolean active) {
    this.id = id;
    this.email = email;
    this.createdAt = createdAt;
    this.active = active;
  }

  public static Organization create(String email) {
    var organization = new Organization(UUID.randomUUID(), email, Instant.now(), true);
    organization.apply(
        new OrganizationCreated(
            organization.id, organization.email, organization.active, organization.createdAt, 1));
    return organization;
  }

  @Override
  public UUID aggregateId() {
    return id;
  }

  @Override
  public void whenTemplate(DomainEvent event) {
    switch (event) {
      case OrganizationCreated ev -> {
        id = ev.aggregateId();
        email = ev.email();
        createdAt = ev.occurredAt();
        active = ev.active();
      }
      default -> throw DomainException.badRequest("unhandled event " + event);
    }
  }

  public UUID getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public boolean isActive() {
    return active;
  }
}
