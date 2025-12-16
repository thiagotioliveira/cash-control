package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.domain.user.UserSummary;
import java.util.Optional;

public interface UserRepository {

  boolean existsByEmail(String email);

  Optional<UserSummary> findByEmailAndPassword(String email, String password);
}
