package dev.thiagooliveira.cashcontrol.infrastructure.persistence.category;

import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {

  boolean existsByNameAndType(String name, TransactionType type);

  Optional<CategoryEntity> findByTypeAndDefaultCategoryIsTrue(TransactionType type);
}
