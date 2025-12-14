package dev.thiagooliveira.cashcontrol.domain.category;

import dev.thiagooliveira.cashcontrol.domain.Aggregate;
import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.domain.event.category.CategoryCreated;
import dev.thiagooliveira.cashcontrol.domain.exception.DomainException;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.logging.log4j.util.Strings;

public class Category extends Aggregate {
  private static final Pattern HEX_COLOR = Pattern.compile("^#?[0-9a-fA-F]{6}$");

  private UUID id;
  private String name;
  private String hashColor;
  private TransactionType type;

  private Category(UUID id, String name, String hashColor, TransactionType type) {
    this.id = id;
    this.name = name;
    this.hashColor = hashColor;
    this.type = type;
  }

  private Category(String name, String hashColor, TransactionType type) {
    this(UUID.randomUUID(), name, hashColor, type);
  }

  public static Category create(
      UUID organizationId, String name, String hashColor, TransactionType type) {
    if (Strings.isBlank(name)) throw DomainException.badRequest("name is required");
    if (type == null) throw DomainException.badRequest("type is required");
    if (!isHexColor(hashColor)) throw DomainException.badRequest("Invalid hex color");
    var category = new Category(name, hashColor, type);
    category.apply(
        new CategoryCreated(
            category.id,
            category.name,
            category.hashColor,
            category.type,
            organizationId,
            Instant.now(),
            1));
    return category;
  }

  private static boolean isHexColor(String value) {
    return Strings.isNotBlank(value) && HEX_COLOR.matcher(value).matches();
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

  @Override
  public UUID aggregateId() {
    return id;
  }

  @Override
  public void whenTemplate(DomainEvent event) {
    switch (event) {
      case CategoryCreated ev -> {
        id = ev.aggregateId();
        name = ev.name();
        hashColor = ev.hashColor();
        type = ev.type();
      }
      default -> throw DomainException.badRequest("unhandled event " + event);
    }
  }
}
