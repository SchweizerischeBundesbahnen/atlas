package ch.sbb.atlas.location.repository;

import ch.sbb.atlas.location.entity.AllocatedNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SloidRepository extends JpaRepository<AllocatedNumberEntity, Integer> {

  @Query(nativeQuery = true, value = "select nextval('service_point_sloid_seq');")
  int getNextSPSeqValue();

  boolean existsByNumber(int number);

}
