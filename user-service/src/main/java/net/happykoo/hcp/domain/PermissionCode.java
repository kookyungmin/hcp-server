package net.happykoo.hcp.domain;

public record PermissionCode(
    String value
) {

  public PermissionCode {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("permission code is required");
    }
  }
}
