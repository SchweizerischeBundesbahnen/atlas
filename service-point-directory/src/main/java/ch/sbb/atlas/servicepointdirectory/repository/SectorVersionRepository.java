package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepointdirectory.entity.SectorVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorVersionRepository extends
    JpaRepository<SectorVersion, Long>, JpaSpecificationExecutor<SectorVersion> {

  @Query(nativeQuery = true,
      value = "select sv.* from sector_version sv "
          + "join sector_group_relations sgr on sv.sloid= sgr.sector_group_sloid "
          + "join sector_group_version sgv on sgv.sloid = sgr.sector_sloid "
          + "where sv.id = :sectorId"
  )
  List<SectorVersion> findAllBySectorId(Long sectorId);

  @Query("""
        SELECT s FROM sector_version s
          LEFT JOIN FETCH s.sectorGroupVersions
      """)
  List<SectorVersion> findAllWithGroups();
}
