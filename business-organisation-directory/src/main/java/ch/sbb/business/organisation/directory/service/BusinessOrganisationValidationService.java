package ch.sbb.business.organisation.directory.service;

import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.exception.BusinessOrganisationAbbreviationConflictException;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class BusinessOrganisationValidationService {

  private final BusinessOrganisationVersionRepository versionRepository;

  public void validateLinePreconditionBusinessRule(BusinessOrganisationVersion version) {
    validateLineConflict(version);
  }

  void validateLineConflict(BusinessOrganisationVersion version) {
    List<BusinessOrganisationVersion> overlaps = versionRepository.findAbbreviationOverlaps(
        version);
    if (!overlaps.isEmpty()) {
      throw new BusinessOrganisationAbbreviationConflictException(version, overlaps);
    }
  }

}
