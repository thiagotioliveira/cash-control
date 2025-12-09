package dev.thiagooliveira.cashcontrol.application.outbound;

public interface UserRepository {

  boolean existsByEmail(String email);
}
