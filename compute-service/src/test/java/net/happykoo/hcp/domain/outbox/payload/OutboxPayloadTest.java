package net.happykoo.hcp.domain.outbox.payload;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import net.happykoo.hcp.domain.instance.InstanceStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OutboxPayloadTest {

  @Test
  @DisplayName("outbox payload record :: 생성자 값이 그대로 보존")
  void constructorTest1() {
    InstanceNetworkPolicyEventPayload policyPayload =
        new InstanceNetworkPolicyEventPayload("instance-1", "ssh", "INGRESS", "22", "0.0.0.0/0");
    InstanceProvisioningEventPayload provisioningPayload = new InstanceProvisioningEventPayload(
        "instance-1", "owner-1", "ubuntu", "ALLOW_ALL", "DENY_ALL", "10.0.0.0/24", "2", "4Gi",
        "SSD", 50
    );
    InstanceRegisterSshKeyEventPayload sshKeyPayload =
        new InstanceRegisterSshKeyEventPayload("instance-1", "ssh-rsa AAAA");
    InstanceScalingEventPayload scalingPayload =
        new InstanceScalingEventPayload("instance-1", "owner-1", "4", "8Gi", "SSD", 100);
    InstanceUpdateLifecycleEventPayload lifecyclePayload =
        new InstanceUpdateLifecycleEventPayload("instance-1", "owner-1", InstanceStatus.RUNNING);
    InstanceUpdateNetworkPolicyEventPayload updatePolicyPayload =
        new InstanceUpdateNetworkPolicyEventPayload("instance-1", List.of(policyPayload));

    assertEquals("ssh", policyPayload.name());
    assertEquals("ubuntu", provisioningPayload.imageName());
    assertEquals("ssh-rsa AAAA", sshKeyPayload.sshKey());
    assertEquals("4", scalingPayload.cpu());
    assertEquals(InstanceStatus.RUNNING, lifecyclePayload.instanceStatus());
    assertEquals(1, updatePolicyPayload.networkPolicies().size());
  }
}
