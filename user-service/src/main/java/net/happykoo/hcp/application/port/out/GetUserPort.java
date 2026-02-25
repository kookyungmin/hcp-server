package net.happykoo.hcp.application.port.out;

import java.util.Optional;
import java.util.UUID;
import net.happykoo.hcp.domain.User;

public interface GetUserPort {

  Optional<User> getUserById(UUID id);
}
