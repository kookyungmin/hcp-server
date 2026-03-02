package net.happykoo.hcp.domain;

// [서비스]:[권한(실행, 수정, 권한부여)]
// (user:read, user:write, user:grant)
public record PermissionCode(
    String value
) {

  public PermissionCode {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("권한 코드는 필수입니다.");
    }
  }
}
