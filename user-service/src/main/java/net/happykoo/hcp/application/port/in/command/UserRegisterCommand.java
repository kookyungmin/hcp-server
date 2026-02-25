package net.happykoo.hcp.application.port.in.command;

public record UserRegisterCommand(
    String email,
    String password,
    String displayName
) {

}
