package ch.sbb.line.directory.service;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.exception.TimetableFieldNumberConflictException;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimetableFieldNumberValidationService {


  private final TimetableFieldNumberVersionRepository versionRepository;
  private final SharedBusinessOrganisationService sharedBusinessOrganisationService;

  public void validatePreSave(TimetableFieldNumberVersion newVersion) {
    validateNoOverlapsOnNumberAndSttfn(newVersion);
    sharedBusinessOrganisationService.validateSboidExists(newVersion.getBusinessOrganisation());
  }

  private void validateNoOverlapsOnNumberAndSttfn(TimetableFieldNumberVersion newVersion) {
    List<TimetableFieldNumberVersion> overlappingVersions = getOverlapsOnNumberAndSttfn(newVersion);
    if (!overlappingVersions.isEmpty()) {
      throw new TimetableFieldNumberConflictException(newVersion, overlappingVersions);
    }
  }

  private List<TimetableFieldNumberVersion> getOverlapsOnNumberAndSttfn(TimetableFieldNumberVersion version) {
    String ttfnid = version.getTtfnid() == null ? "" : version.getTtfnid();
    return versionRepository.getAllByNumberOrSwissTimetableFieldNumberWithValidityOverlap(
            version.getNumber(), version.getSwissTimetableFieldNumber().toLowerCase(),
            version.getValidFrom(), version.getValidTo(), ttfnid).stream()
        .filter(i -> i.getStatus() != Status.REVOKED)
        .toList();
  }

}
