package net.happykoo.hcp.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.out.persistence.jpa.JpaUserRepository;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaUserEntity;
import net.happykoo.hcp.application.port.out.GetUserPort;
import net.happykoo.hcp.application.port.out.SaveUserPort;
import net.happykoo.hcp.application.port.out.data.UserProfile;
import net.happykoo.hcp.common.web.annotation.PersistenceAdapter;
import net.happykoo.hcp.domain.User;

@PersistenceAdapter
@RequiredArgsConstructor
public class UserPersistenceAdapter implements GetUserPort, SaveUserPort {

  private final JpaUserRepository jpaUserRepository;

  @Override
  public Optional<User> getUserById(UUID userId) {
    return jpaUserRepository.findByUserId(userId)
        .map(JpaUserEntity::toDomain);
  }

  @Override
  public Optional<UserProfile> getUserProfileById(UUID userId) {
    return jpaUserRepository.findUserProfileByUserId(userId)
        .map(projection -> new UserProfile(
            projection.getDisplayName()
        ));
  }

  @Override
  public void save(User user) {
    var jpaUserEntity = JpaUserEntity.from(user);
    jpaUserRepository.save(jpaUserEntity);
  }
}
