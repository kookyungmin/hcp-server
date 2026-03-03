package net.happykoo.hcp.adapter.out.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import net.happykoo.hcp.application.port.out.GeneratePayloadHashPort;
import net.happykoo.hcp.common.annotation.SecurityOutAdapter;

@SecurityOutAdapter
public class CryptoAdapter implements GeneratePayloadHashPort {

  @Override
  public String generateSha256Hash(String payload) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
      StringBuilder hex = new StringBuilder(hash.length * 2);
      for (byte b : hash) {
        hex.append(String.format("%02x", b));
      }
      return hex.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 algorithm is not available", e);
    }
  }
}
