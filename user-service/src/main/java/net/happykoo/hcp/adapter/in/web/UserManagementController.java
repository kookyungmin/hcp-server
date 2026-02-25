package net.happykoo.hcp.adapter.in.web;

import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.common.web.response.CommonResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user-management")
@RequiredArgsConstructor
public class UserManagementController {

  @PostMapping("/register")
  public CommonResponseEntity<Void> register() {
    return CommonResponseEntity.ok();
  }

}
