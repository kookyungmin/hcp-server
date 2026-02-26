package net.happykoo.hcp.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.out.persistence.jpa.JpaUserAccountRepository;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaUserAccountEntity;
import net.happykoo.hcp.application.port.out.GetUserAccountPort;
import net.happykoo.hcp.application.port.out.SaveUserAccountPort;
import net.happykoo.hcp.application.port.out.data.UserAccountView;
import net.happykoo.hcp.common.web.annotation.PersistenceAdapter;
import net.happykoo.hcp.domain.UserAccount;

@PersistenceAdapter
@RequiredArgsConstructor
public class UserAccountPersistenceAdapter implements GetUserAccountPort, SaveUserAccountPort {

  private final JpaUserAccountRepository jpaUserAccountRepository;

  @Override
  public Optional<UserAccount> getUserAccountByEmail(String email) {
    return jpaUserAccountRepository.findByEmail(email)
        .map(JpaUserAccountEntity::toDomain);
  }

  @Override
  public boolean existsByEmail(String email) {
    return jpaUserAccountRepository.existsByEmail(email);
  }

  @Override
  public Optional<UserAccountView> getUserAccountViewById(UUID userId) {
    var userAccountProjection = jpaUserAccountRepository.findAccountViewByUserId(userId);
    return userAccountProjection
        .map(projection -> new UserAccountView(
            projection.getUserId(),
            projection.getEmail(),
            projection.getLastChangedPasswordAt()
        ));
  }

  @Override
  public void save(UserAccount userAccount) {
    var entity = JpaUserAccountEntity.from(userAccount);
    jpaUserAccountRepository.save(entity);
  }
}
