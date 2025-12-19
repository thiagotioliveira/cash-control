package dev.thiagooliveira.cashcontrol.application.user;

import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import java.util.Random;

public class PasswordValidator {

  public static boolean isValid(String password) {
    if (password == null || password.isEmpty()) {
      throw ApplicationException.badRequest("Password cannot be empty");
    }

    if (password.length() < 6 || password.length() > 8) {
      throw ApplicationException.badRequest("Password must be between 6 and 8 characters");
    }

    boolean hasLetter = password.matches("^[a-zA-Z0-9]*$");

    if (!hasLetter) {
      throw ApplicationException.badRequest("Password must contain only letters and numbers");
    }

    return true;
  }

  public static String generatePassword() {
    Random random = new Random();
    int length = random.nextInt(6, 9);
    StringBuilder password = new StringBuilder();
    String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    for (int i = 0; i < length; i++) {
      password.append(chars.charAt(random.nextInt(chars.length())));
    }

    return password.toString();
  }
}
