package net.happykoo.hcp.application.port.out;

public interface GeneratePayloadHashPort {

  String generateSha256Hash(String payload);
}
