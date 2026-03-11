package net.happykoo.hcp.adapter.out.orchestrator.openapi;

import io.kubernetes.client.Exec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public record K8sTerminalSession(
    Exec.ExecProcess process,
    OutputStream stdin,
    InputStream stdout,
    InputStream stderr
) {

  public void close() {
    try {
      stdin.close();
    } catch (IOException ignored) {
    }
    try {
      stdout.close();
    } catch (IOException ignored) {
    }
    try {
      stderr.close();
    } catch (IOException ignored) {
    }
    process.destroy();
  }

}
