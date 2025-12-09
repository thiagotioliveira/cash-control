package dev.thiagooliveira.cashcontrol.infrastructure.persistence.bank;

import dev.thiagooliveira.cashcontrol.domain.bank.Bank;
import dev.thiagooliveira.cashcontrol.domain.event.bank.BankCreated;
import dev.thiagooliveira.cashcontrol.shared.Currency;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "banks")
public class BankEntity {
  @Id private UUID id;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Currency currency;

  public BankEntity() {}

  public BankEntity(BankCreated event) {
    this.id = event.bankId();
    this.name = event.name();
    this.currency = event.currency();
  }

  public Bank toDomain() {
    return Bank.restore(this.getId(), this.getName(), this.getCurrency());
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

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }
}
