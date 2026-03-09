package net.happykoo.hcp.adapter.out.persistence.jpa.querydsl;

import static net.happykoo.hcp.adapter.out.persistence.jpa.entity.QJpaInstanceEntity.jpaInstanceEntity;
import static net.happykoo.hcp.adapter.out.persistence.jpa.entity.QJpaInstanceImageEntity.jpaInstanceImageEntity;
import static net.happykoo.hcp.adapter.out.persistence.jpa.entity.QJpaInstanceSpecEntity.jpaInstanceSpecEntity;
import static net.happykoo.hcp.adapter.out.persistence.jpa.entity.QJpaInstanceTagEntity.jpaInstanceTagEntity;
import static net.happykoo.hcp.adapter.out.persistence.jpa.entity.QJpaNetworkVpcEntity.jpaNetworkVpcEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.out.persistence.jpa.JpaInstancePagedQueryRepository;
import net.happykoo.hcp.adapter.out.persistence.jpa.condition.JpaInstanceQueryCondition;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaInstanceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaInstanceQueryDslRepository implements JpaInstancePagedQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public Page<JpaInstanceEntity> findPagedInstance(
      JpaInstanceQueryCondition condition,
      Pageable pageable
  ) {
    var uuids = jpaQueryFactory
        .select(jpaInstanceEntity.instanceId)
        .from(jpaInstanceEntity)
        .where(
            getQueryBooleanExpressions(condition)
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(getOrderSpecifiers(pageable))
        .fetch();

    var result = jpaQueryFactory
        .selectFrom(jpaInstanceEntity)
        .leftJoin(jpaInstanceEntity.image, jpaInstanceImageEntity).fetchJoin()
        .leftJoin(jpaInstanceEntity.spec, jpaInstanceSpecEntity).fetchJoin()
        .leftJoin(jpaInstanceEntity.vpc, jpaNetworkVpcEntity).fetchJoin()
        .leftJoin(jpaInstanceEntity.tags, jpaInstanceTagEntity).fetchJoin()
        .where(
            jpaInstanceEntity.instanceId.in(uuids)
        )
        .distinct()
        .fetch();

    var total = Optional.ofNullable(jpaQueryFactory
            .select(jpaInstanceEntity.countDistinct())
            .from(jpaInstanceEntity)
            .where(
                getQueryBooleanExpressions(condition)
            )
            .fetchOne())
        .orElse(0L);

    return new PageImpl<>(result, pageable, total);
  }

  private Predicate getQueryBooleanExpressions(JpaInstanceQueryCondition condition) {
    BooleanBuilder builder = new BooleanBuilder();

    if (condition.getOwnerId() != null) {
      builder.and(jpaInstanceEntity.ownerId.eq(condition.getOwnerId()));
    }

    if (condition.getSearchKeyword() != null) {
      BooleanBuilder searchKeywordBuilder = new BooleanBuilder();
      searchKeywordBuilder.or(
          jpaInstanceEntity.name.containsIgnoreCase(condition.getSearchKeyword()));
      searchKeywordBuilder.or(
          jpaInstanceEntity.tags.any().id.tag.containsIgnoreCase(condition.getSearchKeyword()));
      builder.and(searchKeywordBuilder);
    }

    return builder;
  }

  private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
    if (pageable.getSort().isUnsorted()) {
      return new OrderSpecifier[]{
          new OrderSpecifier<>(Order.DESC, jpaInstanceEntity.createdAt)
      };
    }
    return pageable.getSort()
        .stream()
        .map(order -> {
          Order direction = order.isAscending() ? Order.ASC : Order.DESC;
          String property = order.getProperty();

          return switch (property) {
            case "status" -> new OrderSpecifier<>(direction, jpaInstanceEntity.status);
            case "name" -> new OrderSpecifier<>(direction, jpaInstanceEntity.name);
            default -> new OrderSpecifier<>(direction, jpaInstanceEntity.createdAt);
          };
        })
        .toArray(OrderSpecifier[]::new);
  }
}
