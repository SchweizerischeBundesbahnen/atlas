package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorVersionRepository extends
    JpaRepository<SectorVersion, Long>, JpaSpecificationExecutor<SectorVersion> {

  List<SectorVersion> findAllBySloidOrderByValidFrom(String sloid);

  @Modifying(clearAutomatically = true)
  @Query("update sector_version v set v.version = (v.version + 1) where v.sloid = :sloid")
  void incrementVersion(@Param("sloid") String sloid);

}
