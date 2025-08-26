package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.repository;

import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.SectorGroupRelation;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.model.SectorGroupRelationId;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorGroupRelationRepository extends JpaRepository<SectorGroupRelation, SectorGroupRelationId>,
    JpaSpecificationExecutor<SectorGroupRelation> {

  Set<SectorGroupRelation> findBySectorGroupRelationIdSectorGroupSloid(String sectorGroupSloid);
}
