package dev.thiagooliveira.cashcontrol.infrastructure.persistence.user;

import dev.thiagooliveira.cashcontrol.domain.event.user.UserCreated;
import dev.thiagooliveira.cashcontrol.domain.event.user.UserInvited;
import dev.thiagooliveira.cashcontrol.domain.event.user.UserJoined;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {

  @Id private UUID id;
  @Column private UUID organizationId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String email;

  @Column private String password;

  @Column(nullable = false)
  private Instant createdAt;

  @Column private boolean active;

  public UserEntity() {}

  public UserEntity(UserCreated event) {
    this.id = event.userId();
    this.organizationId = null;
    this.name = event.name();
    this.email = event.email();
    this.password = event.password();
    this.createdAt = event.occurredAt();
    this.active = event.active();
  }

  public void invite(UserInvited event) {
    this.organizationId = event.organizationId();
  }

  public void join(UserJoined event) {
    this.active = event.active();
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getOrganizationId() {
    return organizationId;
  }

  public void setOrganizationId(UUID organizationId) {
    this.organizationId = organizationId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
