package net.happykoo.hcp.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import net.happykoo.hcp.application.port.in.command.UserRegisterCommand;
import net.happykoo.hcp.application.port.out.EncryptPasswordPort;
import net.happykoo.hcp.application.port.out.GetPermissionPort;
import net.happykoo.hcp.application.port.out.GetUserAccountPort;
import net.happykoo.hcp.application.port.out.SaveUserAccountPort;
import net.happykoo.hcp.application.port.out.SaveUserPort;
import net.happykoo.hcp.domain.PermissionCode;
import net.happykoo.hcp.domain.User;
import net.happykoo.hcp.domain.UserAccount;
import net.happykoo.hcp.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

  @Mock
  private GetUserAccountPort getUserAccountPort;
  @Mock
  private GetPermissionPort getPermissionPort;
  @Mock
  private SaveUserPort saveUserPort;
  @Mock
  private SaveUserAccountPort saveUserAccountPort;
  @Mock
  private EncryptPasswordPort encryptPasswordPort;

  @InjectMocks
  private UserManagementService userManagementService;

  @Test
  @DisplayName("registerMasterUser() :: 신규 마스터 사용자를 생성하고 계정 정보를 저장")
  void registerMasterUserTest1() {
    when(getUserAccountPort.existsByEmail("happykoo@example.com")).thenReturn(false);
    when(getPermissionPort.getAllPermissions())
        .thenReturn(List.of(new PermissionCode("USER_READ"), new PermissionCode("USER_WRITE")));
    when(encryptPasswordPort.encode("plain-password")).thenReturn("encoded-password");

    userManagementService.registerMasterUser(
        new UserRegisterCommand("happykoo@example.com", "plain-password", "happykoo")
    );

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    ArgumentCaptor<UserAccount> accountCaptor = ArgumentCaptor.forClass(UserAccount.class);

    verify(saveUserPort).save(userCaptor.capture());
    verify(saveUserAccountPort).save(accountCaptor.capture());

    User savedUser = userCaptor.getValue();
    UserAccount savedAccount = accountCaptor.getValue();

    assertEquals("happykoo", savedUser.getDisplayName());
    assertEquals(UserStatus.ACTIVE, savedUser.getStatus());
    assertEquals(2, savedUser.getPermissions().size());
    assertEquals(savedUser.getId(), savedAccount.getUserId());
    assertEquals("happykoo@example.com", savedAccount.getEmail().getValue());
    assertEquals("encoded-password", savedAccount.getPasswordHash());
  }

  @Test
  @DisplayName("registerMasterUser() :: 이메일이 이미 존재하면 예외 발생")
  void registerMasterUserTest2() {
    when(getUserAccountPort.existsByEmail("happykoo@example.com")).thenReturn(true);

    assertThrows(
        IllegalStateException.class,
        () -> userManagementService.registerMasterUser(
            new UserRegisterCommand("happykoo@example.com", "plain-password", "happykoo"))
    );

    verify(saveUserPort, never()).save(any());
    verify(saveUserAccountPort, never()).save(any());
  }
}
