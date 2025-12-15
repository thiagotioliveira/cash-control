package dev.thiagooliveira.cashcontrol.infrastructure.persistence.category;

import dev.thiagooliveira.cashcontrol.domain.category.CategorySummary;
import dev.thiagooliveira.cashcontrol.domain.event.category.CategoryCreated;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "categories")
public class CategoryEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private UUID organizationId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String hashColor;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionType type;

  public CategoryEntity() {}

  public CategoryEntity(CategoryCreated event) {
    this.id = event.categoryId();
    this.name = event.name();
    this.hashColor = event.hashColor();
    this.type = event.type();
    this.organizationId = event.organizationId();
  }

  public CategorySummary toDomain() {
    return new CategorySummary(id, name, hashColor, type);
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getHashColor() {
    return hashColor;
  }

  public void setHashColor(String hashColor) {
    this.hashColor = hashColor;
  }

  public TransactionType getType() {
    return type;
  }

  public void setType(TransactionType type) {
    this.type = type;
  }

  public UUID getOrganizationId() {
    return organizationId;
  }

  public void setOrganizationId(UUID organizationId) {
    this.organizationId = organizationId;
  }
}
