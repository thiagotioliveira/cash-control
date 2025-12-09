package dev.thiagooliveira.cashcontrol.application.exception;

public class ApplicationException extends RuntimeException {
  private final int statusCode;

  private ApplicationException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public static ApplicationException badRequest(String message) {
    return new ApplicationException(400, message);
  }

  public static ApplicationException notFound(String message) {
    return new ApplicationException(404, message);
  }
}
