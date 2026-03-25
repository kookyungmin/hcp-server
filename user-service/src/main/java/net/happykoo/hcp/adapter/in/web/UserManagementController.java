package net.happykoo.hcp.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.happykoo.hcp.adapter.in.web.request.UserRegisterRequest;
import net.happykoo.hcp.application.port.in.RegisterUserUseCase;
import net.happykoo.hcp.application.port.in.command.UserRegisterCommand;
import net.happykoo.hcp.common.annotation.WebAdapter;
import net.happykoo.hcp.common.web.response.CommonResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@WebAdapter
@RequestMapping("/v1/user-management")
@RequiredArgsConstructor
@Slf4j
public class UserManagementController {

  private final RegisterUserUseCase registerUserUseCase;

  @PostMapping("/register")
  public CommonResponseEntity<Void> register(
      @RequestBody @Valid UserRegisterRequest request
  ) {
    log.info("사용자 가입 요청을 수신했습니다. email={}", request.email());
    //TODO: email 인증 후 처리해야 함
    registerUserUseCase.registerMasterUser(new UserRegisterCommand(
        request.email(),
        request.password(),
        request.displayName()));
    return CommonResponseEntity.ok();
  }
}
