package dev.thiagooliveira.cashcontrol.domain.category;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.domain.event.category.CategoryCreated;
import dev.thiagooliveira.cashcontrol.domain.exception.DomainException;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Category {
  private UUID id;
  private String name;
  private String hashColor;
  private TransactionType type;
  private boolean defaultCategory = false;

  private int version = 0;
  private final List<DomainEvent> pendingEvents = new ArrayList<>();

  private Category(
      UUID id, String name, String hashColor, TransactionType type, boolean defaultCategory) {
    this.id = id;
    this.name = name;
    this.hashColor = hashColor;
    this.type = type;
    this.defaultCategory = defaultCategory;
  }

  private Category(String name, String hashColor, TransactionType type, boolean defaultCategory) {
    this(UUID.randomUUID(), name, hashColor, type, defaultCategory);
  }

  public static Category restore(
      UUID id, String name, String hashColor, TransactionType type, boolean defaultCategory) {
    return new Category(id, name, hashColor, type, defaultCategory);
  }

  public static Category create(String name, String hashColor, TransactionType type) {
    var category = new Category(name, hashColor, type, false);
    category.apply(
        new CategoryCreated(
            category.id,
            category.name,
            category.hashColor,
            category.type,
            category.defaultCategory,
            Instant.now(),
            1));
    return category;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getHashColor() {
    return hashColor;
  }

  public TransactionType getType() {
    return type;
  }

  public boolean isDefaultCategory() {
    return defaultCategory;
  }

  public List<DomainEvent> pendingEvents() {
    return List.copyOf(pendingEvents);
  }

  public void markEventsCommitted() {
    pendingEvents.clear();
  }

  public int getVersion() {
    return version;
  }

  private void applyFromHistory(DomainEvent e) {
    when(e);
    version++;
  }

  private void apply(DomainEvent event) {
    when(event);
    pendingEvents.add(event);
    version = event.version();
  }

  private void when(DomainEvent event) {
    switch (event) {
      case CategoryCreated ev -> {
        id = ev.aggregateId();
        name = ev.name();
        hashColor = ev.hashColor();
        type = ev.type();
        defaultCategory = ev.defaultCategory();
      }
      default -> throw DomainException.badRequest("unhandled event " + event);
    }
  }
}
