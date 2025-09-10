package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.repository;

import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.SectorGroupVersion;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorGroupVersionRepository extends
    JpaRepository<SectorGroupVersion, Long>, JpaSpecificationExecutor<SectorGroupVersion> {

  List<SectorGroupVersion> findAllBySloidOrderByValidFrom(String sloid);

  List<SectorGroupVersion> findAllByTrafficPointSloid(String trafficPointSloid, Sort sort);

  @Modifying(clearAutomatically = true)
  @Query("update sector_group_version v set v.version = (v.version + 1) where v.sloid = :sloid")
  void incrementVersion(@Param("sloid") String sloid);

}
