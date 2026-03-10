package net.happykoo.hcp.adapter.out.persistence;

import jakarta.persistence.EntityManager;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.out.persistence.jpa.JpaInstancePagedQueryRepository;
import net.happykoo.hcp.adapter.out.persistence.jpa.JpaInstanceRepository;
import net.happykoo.hcp.adapter.out.persistence.jpa.condition.JpaInstanceQueryCondition;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaInstanceEntity;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaInstanceImageEntity;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaInstanceSpecEntity;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaInstanceTagEntity;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaInstanceTagId;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaNetworkVpcEntity;
import net.happykoo.hcp.application.port.out.GetInstanceInfoPort;
import net.happykoo.hcp.application.port.out.SaveInstanceInfoPort;
import net.happykoo.hcp.application.port.out.data.UpdateInstanceStatusData;
import net.happykoo.hcp.common.annotation.PersistenceAdapter;
import net.happykoo.hcp.domain.instance.ServerInstance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@PersistenceAdapter
@RequiredArgsConstructor
public class InstancePersistenceAdapter implements SaveInstanceInfoPort, GetInstanceInfoPort {

  private final EntityManager entityManager;
  private final JpaInstanceRepository jpaInstanceRepository;
  private final JpaInstancePagedQueryRepository jpaInstancePagedQueryRepository;

  @Override
  public ServerInstance saveInstanceInfo(ServerInstance instanceInfo) {
    var entity = JpaInstanceEntity.from(instanceInfo);

    //Tag 셋팅
    instanceInfo.getTags().forEach(tag -> {
      var tagEntity = new JpaInstanceTagEntity(new JpaInstanceTagId(null, tag));
      tagEntity.setInstance(entity);
      entity.getTags().add(tagEntity);
    });

    entity.setImage(entityManager.getReference(JpaInstanceImageEntity.class,
        instanceInfo.getImage().getImageCode()));

    entity.setVpc(entityManager.getReference(JpaNetworkVpcEntity.class,
        instanceInfo.getVpc().getVpcCode()));

    entity.setSpec(entityManager.getReference(JpaInstanceSpecEntity.class,
        instanceInfo.getSpec().getSpecCode()));

    jpaInstanceRepository.saveAndFlush(entity);
    entityManager.clear();

    return jpaInstanceRepository.findWithAllByInstanceId(entity.getInstanceId())
        .map(JpaInstanceEntity::toDomain)
        .orElseThrow(() -> new IllegalStateException("인스턴스 저장이 실패했습니다."));
  }

  @Override
  @Transactional
  public void updateInstanceStatus(UpdateInstanceStatusData updateInstanceStatusData) {
    var entity = jpaInstanceRepository.findById(updateInstanceStatusData.instanceId())
        .orElseThrow(() -> new IllegalStateException("존재하지 않는 인스턴스입니다."));

    entity.setStatus(updateInstanceStatusData.status());
    entity.setFailureReason(updateInstanceStatusData.failureReason());
    entity.setPublicIp(updateInstanceStatusData.publicIp());
    entity.setPrivateIp(updateInstanceStatusData.privateIp());
  }

  @Override
  public Page<ServerInstance> findPagedInstanceByOwnerIdAndSearchKeyword(
      UUID ownerId,
      String searchKeyword,
      Pageable pageable
  ) {
    return jpaInstancePagedQueryRepository.findPagedInstance(
            JpaInstanceQueryCondition.builder()
                .ownerId(ownerId)
                .searchKeyword(searchKeyword)
                .build(),
            pageable
        )
        .map(JpaInstanceEntity::toDomain);
  }

  @Override
  public boolean existsByInstanceId(UUID ownerId, UUID instanceId) {
    return jpaInstanceRepository.existsByInstanceIdAndOwnerId(instanceId, ownerId);
  }
}
