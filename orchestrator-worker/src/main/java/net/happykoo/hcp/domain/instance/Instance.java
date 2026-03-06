package net.happykoo.hcp.domain.instance;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Instance {

  private UUID instanceId;
  private UUID ownerId;
  private String imageName;
  private DefaultNetworkPolicy defaultEgressPolicy;
  private DefaultNetworkPolicy defaultIngressPolicy;
  private String cidrBlock;
  private String cpu;
  private String memory;
  private String storageType;
  private int storageSize;
}
