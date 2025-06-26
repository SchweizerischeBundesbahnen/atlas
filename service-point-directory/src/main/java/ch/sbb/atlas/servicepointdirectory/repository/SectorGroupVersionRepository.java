package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepointdirectory.entity.SectorGroupVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorGroupVersionRepository extends
    JpaRepository<SectorGroupVersion, Long>, JpaSpecificationExecutor<SectorGroupVersion> {

}
