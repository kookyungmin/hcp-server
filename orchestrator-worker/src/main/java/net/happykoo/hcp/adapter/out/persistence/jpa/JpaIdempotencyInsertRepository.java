package net.happykoo.hcp.adapter.out.persistence.jpa;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class JpaIdempotencyInsertRepository {

  private final EntityManager entityManager;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public <T> void tryInsert(T entity) {
    try {
      entityManager.persist(entity);
      entityManager.flush();
    } catch (Exception e) {
      entityManager.clear();
      throw e;
    }
  }
}
