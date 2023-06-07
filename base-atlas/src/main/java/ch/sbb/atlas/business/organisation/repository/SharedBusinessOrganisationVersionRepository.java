package ch.sbb.atlas.business.organisation.repository;

import ch.sbb.atlas.business.organisation.entity.SharedBusinessOrganisationVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedBusinessOrganisationVersionRepository extends JpaRepository<SharedBusinessOrganisationVersion, Long> {

  boolean existsBySboid(String sboid);

}
