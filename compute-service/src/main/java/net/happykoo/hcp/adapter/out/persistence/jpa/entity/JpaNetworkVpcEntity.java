package net.happykoo.hcp.adapter.out.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.happykoo.hcp.domain.network.DefaultNetworkPolicy;
import net.happykoo.hcp.domain.network.NetworkVpc;

@Entity
@Table(name = "h_network_vpc")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JpaNetworkVpcEntity extends JpaTimeBaseEntity {

  @Id
  @Column(name = "vpc_code")
  private String vpcCode;

  @Column(name = "vpc_name", nullable = false)
  private String vpcName;

  @Column(name = "vpc_description")
  private String vpcDescription;

  @Column(name = "cidr_block")
  private String cidrBlock;

  @Column(name = "default_egress_policy")
  private String defaultEgressPolicy;

  @Column(name = "default_ingress_policy")
  private String defaultIngressPolicy;

  public static JpaNetworkVpcEntity from(NetworkVpc vpc) {
    return new JpaNetworkVpcEntity(
        vpc.getVpcCode(),
        vpc.getName(),
        vpc.getDescription(),
        vpc.getCidrBlock(),
        vpc.getDefaultEgressPolicy() == null ? null : vpc.getDefaultEgressPolicy().name(),
        vpc.getDefaultIngressPolicy() == null ? null : vpc.getDefaultIngressPolicy().name()
    );
  }

  public NetworkVpc toDomain() {
    return new NetworkVpc(
        vpcCode,
        vpcName,
        vpcDescription,
        toPolicy(defaultEgressPolicy),
        toPolicy(defaultIngressPolicy),
        cidrBlock
    );
  }

  private DefaultNetworkPolicy toPolicy(String policy) {
    if (policy == null || policy.isBlank()) {
      return null;
    }
    return DefaultNetworkPolicy.valueOf(policy.trim().toUpperCase().replace('-', '_'));
  }
}
