package dev.thiagooliveira.cashcontrol.application.outbound;

public interface BankRepository {

  boolean existsByName(String name);
}
