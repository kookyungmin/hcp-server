package net.happykoo.hcp.application.port.out;

import net.happykoo.hcp.domain.User;

public interface SaveUserPort {

  void save(User user);

}
