package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorGroupRelation;
import ch.sbb.atlas.servicepointdirectory.model.SectorGroupRelationId;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorGroupRelationRepository extends JpaRepository<SectorGroupRelation, SectorGroupRelationId>,
    JpaSpecificationExecutor<SectorGroupRelation> {

  Set<SectorGroupRelation> findBySectorGroupRelationIdSectorGroupSloid(String sectorGroupSloid);
}
