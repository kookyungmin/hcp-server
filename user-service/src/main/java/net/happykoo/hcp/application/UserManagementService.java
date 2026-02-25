package net.happykoo.hcp.application;

import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.in.RegisterUserUseCase;
import net.happykoo.hcp.application.port.in.command.UserRegisterCommand;
import net.happykoo.hcp.application.port.out.EncryptPasswordPort;
import net.happykoo.hcp.application.port.out.GetPermissionPort;
import net.happykoo.hcp.application.port.out.GetUserAccountPort;
import net.happykoo.hcp.application.port.out.SaveUserAccountPort;
import net.happykoo.hcp.application.port.out.SaveUserPort;
import net.happykoo.hcp.common.annotation.UseCase;
import net.happykoo.hcp.domain.User;
import net.happykoo.hcp.domain.UserAccount;
import net.happykoo.hcp.domain.UserStatus;

@UseCase
@RequiredArgsConstructor
public class UserManagementService implements RegisterUserUseCase {

  private final GetUserAccountPort getUserAccountPort;
  private final GetPermissionPort getPermissionPort;
  private final SaveUserPort saveUserPort;
  private final SaveUserAccountPort saveUserAccountPort;
  private final EncryptPasswordPort encryptPasswordPort;

  @Override
  @Transactional
  public void registerMasterUser(UserRegisterCommand command) {
    //TODO: 이메일 인증번호 체크 로직

    //이메일 중복 체크
    var existsEmail = getUserAccountPort.existsByEmail(command.email());
    if (existsEmail) {
      throw new IllegalStateException("Email already exists.");
    }

    //Default Permission 조회
    var permissions = getPermissionPort.getAllPermissions();

    //User 생성 & 권한 부여 & 저장
    var user = User.createMasterUser(
        UUID.randomUUID(),
        command.displayName(),
        UserStatus.ACTIVE);
    user.addPermissions(permissions);

    saveUserPort.save(user);

    //계정 정보 저장
    var account = new UserAccount(user.getId(), command.email(),
        encryptPasswordPort.encode(command.password()));

    saveUserAccountPort.save(account);
  }
}
