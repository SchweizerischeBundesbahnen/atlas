package ch.sbb.business.organisation.directory.module.businessorganisation.service;

import ch.sbb.atlas.model.Status;
import ch.sbb.business.organisation.directory.module.businessorganisation.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.module.businessorganisation.exception.BusinessOrganisationConflictException;
import ch.sbb.business.organisation.directory.module.businessorganisation.repository.BusinessOrganisationVersionRepository;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class BusinessOrganisationValidationService {

  private final BusinessOrganisationVersionRepository versionRepository;

  public void validatePreconditionBusinessRule(BusinessOrganisationVersion version) {
    validateAbbreviationAndOrganisationNumberConflict(version);
  }

  private void validateAbbreviationAndOrganisationNumberConflict(BusinessOrganisationVersion version) {
    List<BusinessOrganisationVersion> overlaps = findAbbreviationAndOrganisationNumberOverlaps(
        version);
    if (!overlaps.isEmpty()) {
      throw new BusinessOrganisationConflictException(version, overlaps);
    }
  }

  List<BusinessOrganisationVersion> findAbbreviationAndOrganisationNumberOverlaps(
      BusinessOrganisationVersion version) {
    return Stream.of(
            versionRepository.findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationDe(
                version.getValidFrom(), version.getValidTo(), version.getAbbreviationDe()),
            versionRepository.findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationFr(
                version.getValidFrom(), version.getValidTo(), version.getAbbreviationFr()),
            versionRepository.findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationIt(
                version.getValidFrom(), version.getValidTo(), version.getAbbreviationIt()),
            versionRepository.findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationEn(
                version.getValidFrom(), version.getValidTo(), version.getAbbreviationEn()),
            versionRepository.findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndOrganisationNumber(
                version.getValidFrom(), version.getValidTo(), version.getOrganisationNumber())
        )
        .flatMap(Collection::stream)
        .filter(i -> !i.getSboid().equals(version.getSboid()))
        .filter(v -> v.getStatus() != Status.REVOKED)
        .distinct()
        .toList();
  }

}
