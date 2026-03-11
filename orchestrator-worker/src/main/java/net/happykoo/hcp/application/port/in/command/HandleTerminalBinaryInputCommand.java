package net.happykoo.hcp.application.port.in.command;

public record HandleTerminalBinaryInputCommand(
    String sessionId,
    byte[] bytes
) {

}
