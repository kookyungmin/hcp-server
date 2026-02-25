package net.happykoo.hcp.application.port.in.command;

public record LoginCommand(
    String email,
    String password
) {

}
