package net.happykoo.hcp.adapter.out.persistence.jpa.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "h_instance_tag")
@NoArgsConstructor
@Getter
@Setter
public class JpaInstanceTagEntity {

  @EmbeddedId
  private JpaInstanceTagId id;

  @MapsId("instanceId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "instance_id", nullable = false)
  private JpaInstanceEntity instance;

  public JpaInstanceTagEntity(JpaInstanceTagId id) {
    this.id = id;
  }
}
