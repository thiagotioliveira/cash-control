package dev.thiagooliveira.cashcontrol.application.user.dto;

import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.user.EmailValidator;
import dev.thiagooliveira.cashcontrol.application.user.PasswordValidator;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public record RegisterUserCommand(
    String name, String email, String password, String passwordConfirmation) {

  public RegisterUserCommand {
    if (StringUtils.isBlank(name)) {
      throw ApplicationException.badRequest("Name is required");
    }
    if (!EmailValidator.isValidEmail(email)) {
      throw ApplicationException.badRequest("Invalid email");
    }
    if (StringUtils.isBlank(password)) {
      throw ApplicationException.badRequest("Password is required");
    }

    PasswordValidator.isValid(password);

    if (!Objects.equals(password, passwordConfirmation)) {
      throw ApplicationException.badRequest("passwords must match");
    }
  }
}
