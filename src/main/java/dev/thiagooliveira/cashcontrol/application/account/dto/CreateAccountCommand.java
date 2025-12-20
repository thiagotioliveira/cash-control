package dev.thiagooliveira.cashcontrol.application.account.dto;

import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

public record CreateAccountCommand(UUID organizationId, UUID bankId, String name) {

  public CreateAccountCommand {
    if (Objects.isNull(organizationId)) {
      throw ApplicationException.badRequest("organizationId must not be null");
    }
    if (Objects.isNull(bankId)) {
      throw ApplicationException.badRequest("bankId must not be null");
    }
    if (StringUtils.isBlank(name)) {
      throw ApplicationException.badRequest("Account name is required");
    }
  }
}
