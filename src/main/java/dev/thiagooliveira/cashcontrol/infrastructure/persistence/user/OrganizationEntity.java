package dev.thiagooliveira.cashcontrol.infrastructure.persistence.user;

import dev.thiagooliveira.cashcontrol.application.user.dto.GetOrganizationItem;
import dev.thiagooliveira.cashcontrol.domain.event.user.OrganizationCreated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "organizations")
public class OrganizationEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private Instant createdAt;

  @Column private boolean active;

  public OrganizationEntity() {}

  public OrganizationEntity(OrganizationCreated event) {
    this.id = event.organizationId();
    this.email = event.email();
    this.createdAt = event.occurredAt();
    this.active = event.active();
  }

  public GetOrganizationItem toDomain() {
    return new GetOrganizationItem(id, email, createdAt, active);
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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
