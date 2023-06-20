package ch.sbb.atlas.business.organisation.repository;

import ch.sbb.atlas.kafka.model.business.organisation.SharedBusinessOrganisationVersionModel;

public interface BusinessOrganisationVersionSharingDataAccessor {

  boolean existsBySboid(String sboid);

  void deleteById(Long id);

  void save(SharedBusinessOrganisationVersionModel model);
}
