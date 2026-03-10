package net.happykoo.hcp.application.port.in.command;

public record HandleTerminalControlCommand(
    String sessionId,
    String message
) {

}
