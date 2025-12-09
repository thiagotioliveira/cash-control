package dev.thiagooliveira.cashcontrol.infrastructure.exception;

public class InfrastructureException extends RuntimeException {
  private final int statusCode;

  private InfrastructureException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  private InfrastructureException(int statusCode, String message, Exception e) {
    super(message, e);
    this.statusCode = statusCode;
  }

  public static InfrastructureException conflict(String message) {
    return new InfrastructureException(409, message);
  }

  public static InfrastructureException internalError(String message, Exception e) {
    return new InfrastructureException(500, message);
  }

  public static InfrastructureException notFound(String message) {
    return new InfrastructureException(404, message);
  }

  public static InfrastructureException badRequest(String message) {
    return new InfrastructureException(400, message);
  }

  public int getStatusCode() {
    return statusCode;
  }
}
