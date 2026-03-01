package net.happykoo.hcp.domain.instance;

import java.util.Set;
import java.util.UUID;
import net.happykoo.hcp.domain.network.InstanceVpc;

public class ServerInstance {

  private UUID instanceId;
  private UUID ownerId;
  private String name;
  private Set<String> tags;
  private InstanceImage image;
  private InstanceVpc vpc;
  private InstanceStatus stratus;
  private InstanceSpec spec;
  private InstanceStorage storage;
  private String failureReason;
}
