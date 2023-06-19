package ch.sbb.atlas.business.organisation.service;

import ch.sbb.atlas.business.organisation.repository.BusinessOrganisationVersionSharingDataAccessor;
import ch.sbb.atlas.model.exception.SboidNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SharedBusinessOrganisationService {

  private final BusinessOrganisationVersionSharingDataAccessor businessOrganisationVersionSharingDataAccessor;

  public void validateSboidExists(String sboid) {
    if (!existsBySboid(sboid)) {
      throw new SboidNotFoundException(sboid);
    }
  }

  public boolean existsBySboid(String sboid) {
    return businessOrganisationVersionSharingDataAccessor.existsBySboid(sboid);
  }
}
