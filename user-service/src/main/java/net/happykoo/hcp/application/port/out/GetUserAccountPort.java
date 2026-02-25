package net.happykoo.hcp.application.port.out;

import java.util.Optional;
import net.happykoo.hcp.domain.UserAccount;

public interface GetUserAccountPort {

  Optional<UserAccount> getUserAccountByEmail(String email);

  boolean existsByEmail(String email);
}
