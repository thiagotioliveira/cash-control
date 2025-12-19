package dev.thiagooliveira.cashcontrol.application.user;

import jakarta.mail.internet.InternetAddress;

public class EmailValidator {

  public static boolean isValidEmail(String email) {
    if (email == null || email.isBlank()) return false;

    try {
      InternetAddress address = new InternetAddress(email);
      address.validate();
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
