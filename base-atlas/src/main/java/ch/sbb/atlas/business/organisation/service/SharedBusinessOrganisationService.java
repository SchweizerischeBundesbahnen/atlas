package ch.sbb.atlas.business.organisation.service;

import ch.sbb.atlas.model.exception.SboidNotFoundException;
import ch.sbb.atlas.business.organisation.repository.SharedBusinessOrganisationVersionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SharedBusinessOrganisationService {

  private final SharedBusinessOrganisationVersionRepository sharedBusinessOrganisationVersionRepository;

  public void validateSboidExists(String sboid) {
    if (!existsBySboid(sboid)) {
      throw new SboidNotFoundException(sboid);
    }
  }

  public boolean existsBySboid(String sboid) {
    return sharedBusinessOrganisationVersionRepository.existsBySboid(sboid);
  }
}
