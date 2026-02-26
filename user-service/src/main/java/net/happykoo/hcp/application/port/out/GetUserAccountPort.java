package net.happykoo.hcp.application.port.out;

import java.util.Optional;
import java.util.UUID;
import net.happykoo.hcp.application.port.out.data.UserAccountView;
import net.happykoo.hcp.domain.UserAccount;

public interface GetUserAccountPort {

  Optional<UserAccount> getUserAccountByEmail(String email);

  boolean existsByEmail(String email);

  Optional<UserAccountView> getUserAccountViewById(UUID userId);
}
