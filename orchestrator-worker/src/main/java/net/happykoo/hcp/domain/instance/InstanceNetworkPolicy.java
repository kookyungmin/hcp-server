package net.happykoo.hcp.domain.instance;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InstanceNetworkPolicy {

  private UUID instanceId;
  private NetworkPolicyType type;
  private String policyName;
  private String ipCidr;
  private String port;

}
