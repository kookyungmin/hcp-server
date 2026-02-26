package net.happykoo.hcp.application.port.out;

import java.util.Optional;
import java.util.UUID;
import net.happykoo.hcp.application.port.out.data.UserProfile;
import net.happykoo.hcp.domain.User;

public interface GetUserPort {

  Optional<User> getUserById(UUID userId);

  Optional<UserProfile> getUserProfileById(UUID userId);
}
