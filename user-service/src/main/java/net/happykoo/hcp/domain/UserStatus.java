package net.happykoo.hcp.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserStatus {
  ACTIVE("A"),
  INACTIVE("I"),
  DELETED("D");

  private final String code;
}
