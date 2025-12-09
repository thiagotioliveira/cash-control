package dev.thiagooliveira.cashcontrol.infrastructure.persistence.category;

import dev.thiagooliveira.cashcontrol.domain.category.Category;
import dev.thiagooliveira.cashcontrol.domain.event.category.CategoryCreated;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "categories")
public class CategoryEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String hashColor;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionType type;

  @Column(nullable = false)
  private boolean defaultCategory;

  public CategoryEntity() {}

  public CategoryEntity(CategoryCreated event) {
    this.id = event.categoryId();
    this.name = event.name();
    this.hashColor = event.hashColor();
    this.type = event.type();
    this.defaultCategory = event.defaultCategory();
  }

  public Category toDomain() {
    return Category.restore(id, name, hashColor, type, defaultCategory);
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

  public boolean isDefaultCategory() {
    return defaultCategory;
  }

  public void setDefaultCategory(boolean defaultCategory) {
    this.defaultCategory = defaultCategory;
  }
}
