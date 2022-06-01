package ch.sbb.business.organisation.directory.service;

import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.exception.BusinessOrganisationAbbreviationConflictException;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class BusinessOrganisationValidationService {

  private final BusinessOrganisationVersionRepository versionRepository;

  public void validatePreconditionBusinessRule(BusinessOrganisationVersion version) {
    validateAbbreviationConflict(version);
  }

  private void validateAbbreviationConflict(BusinessOrganisationVersion version) {
    List<BusinessOrganisationVersion> overlaps = findAbbreviationOverlaps(
        version);
    if (!overlaps.isEmpty()) {
      throw new BusinessOrganisationAbbreviationConflictException(version, overlaps);
    }
  }

  List<BusinessOrganisationVersion> findAbbreviationOverlaps(
      BusinessOrganisationVersion version) {
    return Stream.of(
                     versionRepository.findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationDeIgnoreCase(
                                          version.getValidFrom(), version.getValidTo(), version.getAbbreviationDe())
                                      .stream(),
                     versionRepository.findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationFrIgnoreCase(
                                          version.getValidFrom(), version.getValidTo(), version.getAbbreviationFr())
                                      .stream(),
                     versionRepository.findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationItIgnoreCase(
                                          version.getValidFrom(), version.getValidTo(), version.getAbbreviationIt())
                                      .stream(),
                     versionRepository.findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationEnIgnoreCase(
                                          version.getValidFrom(), version.getValidTo(), version.getAbbreviationEn())
                                      .stream())
                 .flatMap(Function.identity())
                 .filter(i -> !i.getSboid().equals(version.getSboid()))
                 .distinct()
                 .collect(Collectors.toList());
  }

}
