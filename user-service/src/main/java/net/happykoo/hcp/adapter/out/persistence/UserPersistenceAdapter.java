package net.happykoo.hcp.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.out.persistence.jpa.JpaUserRepository;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaUserEntity;
import net.happykoo.hcp.application.port.out.GetUserPort;
import net.happykoo.hcp.application.port.out.SaveUserPort;
import net.happykoo.hcp.common.annotation.PersistenceAdapter;
import net.happykoo.hcp.domain.User;

@PersistenceAdapter
@RequiredArgsConstructor
public class UserPersistenceAdapter implements GetUserPort, SaveUserPort {

  private final JpaUserRepository jpaUserRepository;

  @Override
  public Optional<User> getUserById(UUID id) {
    return Optional.empty();
  }

  @Override
  public void save(User user) {
    JpaUserEntity jpaUserEntity = JpaUserEntity.from(user);
    jpaUserRepository.save(jpaUserEntity);
  }
}
