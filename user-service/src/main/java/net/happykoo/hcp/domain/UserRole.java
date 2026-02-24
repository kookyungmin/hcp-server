package net.happykoo.hcp.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserRole {
  SYSTEM("S"),
  ADMIN("A"),
  USER("U");

  private final String code;
}
