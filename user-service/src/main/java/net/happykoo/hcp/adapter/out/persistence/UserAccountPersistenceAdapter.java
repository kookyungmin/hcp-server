package net.happykoo.hcp.adapter.out.persistence;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.out.persistence.jpa.JpaUserAccountRepository;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaUserAccountEntity;
import net.happykoo.hcp.application.port.out.GetUserAccountPort;
import net.happykoo.hcp.application.port.out.SaveUserAccountPort;
import net.happykoo.hcp.common.annotation.PersistenceAdapter;
import net.happykoo.hcp.domain.UserAccount;

@PersistenceAdapter
@RequiredArgsConstructor
public class UserAccountPersistenceAdapter implements GetUserAccountPort, SaveUserAccountPort {

  private final JpaUserAccountRepository jpaUserAccountRepository;

  @Override
  public Optional<UserAccount> getUserAccountByEmail(String email) {
    return Optional.empty();
  }

  @Override
  public boolean existsByEmail(String email) {
    return false;
  }

  @Override
  public void save(UserAccount userAccount) {
    var entity = JpaUserAccountEntity.from(userAccount);
    jpaUserAccountRepository.save(entity);
  }
}
