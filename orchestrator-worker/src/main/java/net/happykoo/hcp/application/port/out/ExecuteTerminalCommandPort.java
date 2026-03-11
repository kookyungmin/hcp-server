package net.happykoo.hcp.application.port.out;

public interface ExecuteTerminalCommandPort {

  void close(String sessionId);

  void open(String sessionId, String namespace, String podName);

  void write(String sessionId, byte[] bytes);

  void resize(String sessionId, Integer cols, Integer rows);
}
