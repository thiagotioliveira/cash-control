package dev.thiagooliveira.cashcontrol.infrastructure.listener.bank;

import dev.thiagooliveira.cashcontrol.domain.event.bank.BankCreated;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.bank.BankEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.bank.BankJpaRepository;
import org.springframework.context.event.EventListener;

public class BankEventListener {

  private final BankJpaRepository repository;

  public BankEventListener(BankJpaRepository repository) {
    this.repository = repository;
  }

  @EventListener
  public void on(BankCreated event) {
    var entity = new BankEntity(event);
    repository.save(entity);
  }
}
