package net.happykoo.hcp.domain.instance;

import java.util.Set;
import java.util.UUID;
import net.happykoo.hcp.domain.network.ServerVpc;

public class ServerInstance {

  private UUID instanceId;
  private UUID ownerId;
  private String instanceName;
  private Set<String> tags;
  private ServerImage serverImage;
  private ServerVpc serverVpc;
  private ServerStatus serverStatus;
  private ServerSpec serverSpec;
  private String failureReason;
}
