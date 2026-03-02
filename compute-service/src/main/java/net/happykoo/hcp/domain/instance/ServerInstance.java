package net.happykoo.hcp.domain.instance;

import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import net.happykoo.hcp.domain.network.NetworkVpc;

@AllArgsConstructor
public class ServerInstance {

  private UUID instanceId;
  private UUID ownerId;
  private String name;
  private Set<String> tags;
  private InstanceImage image;
  private NetworkVpc vpc;
  private InstanceSpec spec;
  private InstanceStorage storage;
  private InstanceStatus status;
  private String failureReason;
  private String publicIp;
  private String privateIp;

  public ServerInstance(
      UUID instanceId,
      UUID ownerId,
      String name,
      Set<String> tags,
      InstanceImage image,
      NetworkVpc vpc,
      InstanceSpec spec,
      InstanceStorage storage,
      InstanceStatus status
  ) {
    this(
        instanceId,
        ownerId,
        name,
        tags,
        image,
        vpc,
        spec,
        storage,
        status,
        null,
        null,
        null
    );
  }
}
