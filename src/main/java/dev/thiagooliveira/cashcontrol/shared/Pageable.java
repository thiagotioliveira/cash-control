package dev.thiagooliveira.cashcontrol.shared;

public record Pageable(int pageNumber, int pageSize) {
  public static Pageable of(int pageNumber, int pageSize) {
    return new Pageable(pageNumber, pageSize);
  }
}
