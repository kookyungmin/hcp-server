package net.happykoo.hcp.application.port.in.command;

import java.util.Set;
import java.util.UUID;
import net.happykoo.hcp.domain.instance.InstanceImage;
import net.happykoo.hcp.domain.instance.InstanceSpec;
import net.happykoo.hcp.domain.instance.InstanceStorage;
import net.happykoo.hcp.domain.network.NetworkVpc;

public record ProvisionInstanceCommand(
    UUID ownerId,
    String name,
    String tags,
    String imageCode,
    String vpcCode,
    String specCode,
    String storageType,
    int storageSize,
    String idempotencyKey,
    String requestId
) {

  public Set<String> tagSet() {
    if (tags == null) {
      return Set.of();
    }
    return Set.of(tags.split(","));
  }

  public InstanceImage image() {
    return new InstanceImage(imageCode);
  }

  public NetworkVpc vpc() {
    return new NetworkVpc(vpcCode);
  }

  public InstanceSpec spec() {
    return new InstanceSpec(specCode);
  }

  public InstanceStorage storage() {
    return new InstanceStorage(storageType, storageSize);
  }

  public String payload() {
    return name
        + tags
        + imageCode
        + vpcCode
        + specCode
        + storageType
        + storageSize;
  }
}
