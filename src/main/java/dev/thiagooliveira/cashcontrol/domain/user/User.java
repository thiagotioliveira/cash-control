package dev.thiagooliveira.cashcontrol.domain.user;

import dev.thiagooliveira.cashcontrol.domain.Aggregate;
import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.domain.event.user.v1.UserCreated;
import dev.thiagooliveira.cashcontrol.domain.event.user.v1.UserInvited;
import dev.thiagooliveira.cashcontrol.domain.event.user.v1.UserJoined;
import dev.thiagooliveira.cashcontrol.domain.exception.DomainException;
import java.time.Instant;
import java.util.UUID;

public class User extends Aggregate {

  private UUID id;
  private UUID organizationId;
  private String name;
  private String email;
  private String password;
  private Instant createdAt;
  private boolean active;

  private User(
      UUID id, String name, String email, String password, Instant createdAt, boolean active) {
    this.id = id;
    this.organizationId = null;
    this.name = name;
    this.email = email;
    this.password = password;
    this.createdAt = createdAt;
    this.active = active;
  }

  public static User create(String name, String email, String password) {
    var user = new User(UUID.randomUUID(), name, email, password, Instant.now(), false);
    user.apply(
        new UserCreated(
            user.id, user.name, user.email, user.password, user.active, user.createdAt, 1));
    return user;
  }

  public static User create(String name, String email) {
    var user = new User(UUID.randomUUID(), name, email, null, Instant.now(), false);
    user.apply(
        new UserCreated(
            user.id, user.name, user.email, user.password, user.active, user.createdAt, 1));
    return user;
  }

  public void invite(UUID organizationId) {
    if (this.organizationId != null) {
      throw DomainException.badRequest("user already in a organization");
    }
    if (this.active) {
      throw DomainException.badRequest("user already active");
    }
    apply(
        new UserInvited(
            this.id, this.name, this.email, organizationId, Instant.now(), getVersion() + 1));
  }

  public void join() {
    if (this.active) {
      throw DomainException.badRequest("user already active");
    }
    if (this.organizationId == null) {
      throw DomainException.badRequest("user not invited");
    }
    apply(
        new UserJoined(
            this.id, this.name, this.organizationId, true, Instant.now(), getVersion() + 1));
  }

  @Override
  public UUID aggregateId() {
    return id;
  }

  @Override
  public void whenTemplate(DomainEvent event) {
    switch (event) {
      case UserCreated ev -> {
        id = ev.aggregateId();
        name = ev.name();
        email = ev.email();
        password = ev.password();
        createdAt = ev.occurredAt();
        active = ev.active();
      }
      case UserInvited ev -> organizationId = ev.organizationId();
      case UserJoined ev -> active = ev.active();
      default -> throw DomainException.badRequest("unhandled event " + event);
    }
  }

  public UUID getId() {
    return id;
  }

  public UUID getOrganizationId() {
    return organizationId;
  }

  public String getName() {
    return name;
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
