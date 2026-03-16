package net.happykoo.hcp.application.port.out.data;


public record InstanceStatusData(
    String instanceId,
    InstanceStatus status,
    String message,
    String publicIp,
    String privateIp
) {

  public static InstanceStatusData success(
      String instanceId,
      String publicIp,
      String privateIp
  ) {
    return new InstanceStatusData(
        instanceId,
        InstanceStatus.SUCCESS,
        null,
        publicIp,
        privateIp
    );
  }

  public static InstanceStatusData deleted(
      String instanceId
  ) {
    return new InstanceStatusData(
        instanceId,
        InstanceStatus.DELETED,
        null,
        null,
        null);
  }

  public static InstanceStatusData failed(
      String instanceId,
      String message
  ) {
    return new InstanceStatusData(
        instanceId,
        InstanceStatus.FAILED,
        message,
        null,
        null);
  }

  public static InstanceStatusData processing(
      String instanceId
  ) {
    return new InstanceStatusData(
        instanceId,
        InstanceStatus.PROCESSING,
        null,
        null,
        null);
  }

  public static InstanceStatusData stopped(String instanceId) {
    return new InstanceStatusData(
        instanceId,
        InstanceStatus.STOPPED,
        null,
        null,
        null
    );
  }
}
