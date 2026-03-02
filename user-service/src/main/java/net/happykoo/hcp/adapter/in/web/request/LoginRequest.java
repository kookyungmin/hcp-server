package net.happykoo.hcp.adapter.in.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z]).{8,}$",
        message = "비밀번호는 대문자 1개 이상, 소문자 1개 이상을 포함하고 8자 이상이어야 합니다."
    )
    String password
) {

}
