package net.happykoo.hcp.application.port.out;

public interface EncryptPasswordPort {

  String encode(String raw);

  boolean matches(String raw, String encoded);
}
