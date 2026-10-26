package ch.sbb.line.directory.module.line.validation;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LineVersionV3ValidationService {

  private final LineV3TypeValidator typeValidator;
  private final LineV3ValidityValidator validityValidator;
  private final LineV3UpdateValidator updateValidator;
  private final SharedBusinessOrganisationService sharedBusinessOrganisationService;

  public void validatePreconditionBusinessRules(LineVersionV3 version) {
    if (isMember(version)) {
      typeValidator.validateMemberType(version);
    } else {
      typeValidator.validateLineType(version);
    }
    typeValidator.validateSwissLineNumberUniqueness(version);
    sharedBusinessOrganisationService.validateSboidExists(version.getBusinessOrganisation());
  }

  public void validateValidityConstraints(List<LineVersionV3> versionsOfSlnid) {
    if (isMember(versionsOfSlnid.getFirst())) {
      validityValidator.validateMemberWithinReferencedLineValidity(versionsOfSlnid);
    } else {
      validityValidator.validateLineTypeDurationLimits(versionsOfSlnid);
    }
  }

  public void validateMembersWithinParentValidity(List<LineVersionV3> parentVersions) {
    validityValidator.validateMembersWithinLineValidity(parentVersions);
  }

  public void validateNotRevoked(LineVersionV3 current) {
    updateValidator.validateNotRevoked(current);
  }

  public void validateUpdate(LineVersionV3 current, LineVersionV3 edited) {
    updateValidator.validateConcessionFieldsUpdatable(current, edited);
  }

  private static boolean isMember(LineVersionV3 version) {
    return version.getReferencedLineSlnid() != null;
  }

}
