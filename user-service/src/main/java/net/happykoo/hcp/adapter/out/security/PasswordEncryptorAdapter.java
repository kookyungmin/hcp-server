package net.happykoo.hcp.adapter.out.security;

import net.happykoo.hcp.application.port.out.EncryptPasswordPort;
import net.happykoo.hcp.common.web.annotation.SecurityOutAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SecurityOutAdapter
public class PasswordEncryptorAdapter implements EncryptPasswordPort {

  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Override
  public String encode(String raw) {
    return passwordEncoder.encode(raw);
  }

  @Override
  public boolean matches(String raw, String encoded) {
    return passwordEncoder.matches(raw, encoded);
  }
}
