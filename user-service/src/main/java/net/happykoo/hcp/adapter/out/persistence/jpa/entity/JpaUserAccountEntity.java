package net.happykoo.hcp.adapter.out.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.happykoo.hcp.common.persistence.jpa.entity.TimeBaseEntity;
import net.happykoo.hcp.domain.UserAccount;

@Entity
@Table(name = "h_user_account")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JpaUserAccountEntity extends TimeBaseEntity {

  @Id
  @Column(name = "user_id")
  private UUID userId;

  @Column(name = "email", unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "last_changed_password_at")
  private Instant lastChangedPasswordAt;

  public static JpaUserAccountEntity from(UserAccount userAccount) {
    return new JpaUserAccountEntity(
        userAccount.getUserId(),
        userAccount.getEmail().getValue(),
        userAccount.getPasswordHash(),
        userAccount.getPasswordChangedAt()
    );
  }

  public UserAccount toDomain() {
    return new UserAccount(userId, email, password, lastChangedPasswordAt);
  }

}
