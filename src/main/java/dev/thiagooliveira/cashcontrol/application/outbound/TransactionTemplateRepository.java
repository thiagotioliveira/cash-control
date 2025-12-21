package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionTemplateSummary;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionTemplateRepository {

  List<TransactionTemplateSummary>
      findAllByOrganizationIdAndAccountIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateIsNull(
          UUID organizationId, UUID accountId, LocalDate startDate, LocalDate endDate);

  List<TransactionTemplateSummary>
      findAllByOrganizationIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateIsNull(
          UUID organizationId, LocalDate startDate, LocalDate endDate);
}
