package net.happykoo.hcp.adapter.out.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.happykoo.hcp.domain.network.NetworkPolicy;
import net.happykoo.hcp.domain.network.NetworkPolicyType;

@Entity
@Table(name = "h_network_policy")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JpaNetworkPolicyEntity extends JpaTimeBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "instance_id")
  private UUID instanceId;

  @Column(name = "policy_name")
  private String policyName;

  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private NetworkPolicyType type;

  @Column(name = "cidr_block")
  private String cidrBlock;

  @Column(name = "port")
  private String port;

  public NetworkPolicy toDomain() {
    return new NetworkPolicy(
        instanceId,
        type,
        policyName,
        cidrBlock,
        port
    );
  }

  public static JpaNetworkPolicyEntity from(NetworkPolicy networkPolicy) {
    return new JpaNetworkPolicyEntity(
        null,
        networkPolicy.getInstanceId(),
        networkPolicy.getPolicyName(),
        networkPolicy.getType(),
        networkPolicy.getIpCidr(),
        networkPolicy.getPort()
    );
  }
}
