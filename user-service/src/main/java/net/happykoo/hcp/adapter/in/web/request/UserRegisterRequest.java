package net.happykoo.hcp.adapter.in.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRegisterRequest(
    @NotBlank(message = "email is required")
    @Email(message = "email is invalid")
    String email,

    @NotBlank(message = "password is required")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z]).{8,}$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, and be at least 8 characters long."
    )
    String password,

    @NotBlank(message = "display name is required")
    String displayName

) {

}
