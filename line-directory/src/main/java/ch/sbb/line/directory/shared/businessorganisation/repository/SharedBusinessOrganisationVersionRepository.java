package ch.sbb.line.directory.shared.businessorganisation.repository;

import ch.sbb.atlas.business.organisation.repository.BusinessOrganisationVersionSharingDataAccessor;
import ch.sbb.atlas.kafka.model.business.organisation.SharedBusinessOrganisationVersionModel;
import ch.sbb.line.directory.shared.businessorganisation.entity.SharedBusinessOrganisationVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedBusinessOrganisationVersionRepository extends JpaRepository<SharedBusinessOrganisationVersion, Long>,
    BusinessOrganisationVersionSharingDataAccessor {

  @Override
  boolean existsBySboid(String sboid);

  List<SharedBusinessOrganisationVersion> findByOrganisationNumber(Integer number);

  @Override
  default void save(SharedBusinessOrganisationVersionModel model) {
    SharedBusinessOrganisationVersion sharedBusinessOrganisationVersion = new SharedBusinessOrganisationVersion();
    sharedBusinessOrganisationVersion.setPropertiesFromModel(model);
    save(sharedBusinessOrganisationVersion);
  }
}
