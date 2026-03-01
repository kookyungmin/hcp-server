package net.happykoo.hcp.domain.instance;

import java.util.Set;
import java.util.UUID;
import net.happykoo.hcp.domain.network.NetworkVpc;

public class ServerInstance {

  private UUID instanceId;
  private UUID ownerId;
  private String name;
  private Set<String> tags;
  private InstanceImage image;
  private NetworkVpc vpc;
  private InstanceStatus status;
  private InstanceSpec spec;
  private InstanceStorage storage;
  private String failureReason;
  private String publicIp;
  private String privateIp;
}
