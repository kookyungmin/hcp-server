package net.happykoo.hcp.application.port.out;

import net.happykoo.hcp.domain.UserAccount;

public interface SaveUserAccountPort {

  void save(UserAccount userAccount);

}
