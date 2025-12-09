package dev.thiagooliveira.cashcontrol.domain.exception;

public class DomainException extends RuntimeException {
  private final int statusCode;

  private DomainException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public static DomainException badRequest(String message) {
    return new DomainException(400, message);
  }

  public static DomainException internalError(String message, Exception e) {
    return new DomainException(500, message);
  }

  public int getStatusCode() {
    return statusCode;
  }
}
