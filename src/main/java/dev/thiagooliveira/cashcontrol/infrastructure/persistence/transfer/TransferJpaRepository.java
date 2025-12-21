package dev.thiagooliveira.cashcontrol.infrastructure.persistence.transfer;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferJpaRepository extends JpaRepository<TransferEntity, UUID> {}
