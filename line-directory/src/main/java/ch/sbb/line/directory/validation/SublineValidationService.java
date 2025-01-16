package ch.sbb.line.directory.validation;

import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.service.OverviewDisplayBuilder;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.exception.RevokedException;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.exception.SubLineAssignToLineConflictException;
import ch.sbb.line.directory.exception.SublineConcessionException;
import ch.sbb.line.directory.exception.SublineConcessionSwissSublineNumberException;
import ch.sbb.line.directory.exception.SublineConflictException;
import ch.sbb.line.directory.exception.SublineTypeMissmatchException;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SublineValidationService {

  private static final Map<LineType, Set<SublineType>> ALLOWED_SUBLINE_TYPES = new EnumMap<>(LineType.class);

  static {
    ALLOWED_SUBLINE_TYPES.put(LineType.ORDERLY, Set.of(SublineType.CONCESSION, SublineType.TECHNICAL));
    ALLOWED_SUBLINE_TYPES.put(LineType.DISPOSITION, Set.of(SublineType.DISPOSITION));
    ALLOWED_SUBLINE_TYPES.put(LineType.TEMPORARY, Set.of(SublineType.TEMPORARY));
    ALLOWED_SUBLINE_TYPES.put(LineType.OPERATIONAL, Set.of(SublineType.OPERATIONAL));
  }

  private final SublineVersionRepository sublineVersionRepository;
  private final LineVersionRepository lineVersionRepository;
  private final CoverageValidationService coverageValidationService;
  private final SharedBusinessOrganisationService sharedBusinessOrganisationService;

  public void validatePreconditionSublineBusinessRules(SublineVersion sublineVersion) {
    validateMainLine(sublineVersion);
    validateSublineConflict(sublineVersion);
    validateDifferentMainLineAssignRule(sublineVersion);
    sharedBusinessOrganisationService.validateSboidExists(sublineVersion.getBusinessOrganisation());
  }

  private void validateMainLine(SublineVersion sublineVersion) {
    List<LineVersion> lineVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(sublineVersion.getMainlineSlnid());
    if (lineVersions.isEmpty()) {
      throw new SlnidNotFoundException(sublineVersion.getMainlineSlnid());
    }
    LineVersion mainline = OverviewDisplayBuilder.getDisplayModel(lineVersions);
    validateNotRevoked(mainline);
    validateSublineType(sublineVersion, mainline);
    validateConcessionType(sublineVersion);
  }

  private void validateNotRevoked(LineVersion mainline) {
    if (mainline.getStatus() == Status.REVOKED) {
      throw new RevokedException(mainline.getSlnid());
    }
  }

  private void validateSublineType(SublineVersion sublineVersion, LineVersion mainline) {
    if (!ALLOWED_SUBLINE_TYPES.get(mainline.getLineType()).contains(sublineVersion.getSublineType())) {
      throw new SublineTypeMissmatchException(sublineVersion.getSublineType(), mainline.getLineType());
    }
  }

  private void validateConcessionType(SublineVersion sublineVersion) {
    if (sublineVersion.getSublineType() == SublineType.CONCESSION ^ sublineVersion.getConcessionType() != null) {
      throw new SublineConcessionException();
    }
    if (sublineVersion.getSublineType() == SublineType.CONCESSION ^ sublineVersion.getSwissSublineNumber() != null) {
      throw new SublineConcessionSwissSublineNumberException();
    }
  }

  public void validateSublineAfterVersioningBusinessRule(SublineVersion sublineVersion) {
    LineVersion lineVersion = lineVersionRepository.findAllBySlnidOrderByValidFrom(
            sublineVersion.getMainlineSlnid())
        .stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "No Line found for the given subline!"));
    coverageValidationService.validateLineSublineCoverage(lineVersion);
  }

  void validateSublineConflict(SublineVersion sublineVersion) {
    if (StringUtils.isNotBlank(sublineVersion.getSwissSublineNumber())) {
      List<SublineVersion> swissLineNumberOverlaps = sublineVersionRepository.findSwissLineNumberOverlaps(
          sublineVersion);
      if (!swissLineNumberOverlaps.isEmpty()) {
        throw new SublineConflictException(sublineVersion, swissLineNumberOverlaps);
      }
    }
  }

  void validateDifferentMainLineAssignRule(SublineVersion sublineVersion) {
    if (sublineVersion.getId() != null) {
      SublineVersion sublineVersionActual =
          sublineVersionRepository.findById(sublineVersion.getId())
              .orElse(null);
      if (sublineVersionActual != null &&
          !sublineVersionActual.getMainlineSlnid().equals(sublineVersion.getMainlineSlnid())) {
        throw new SubLineAssignToLineConflictException(sublineVersionActual);
      }
    }
  }

  boolean validateLineRangeRule(SublineVersion sublineVersion) {
    List<LineVersion> lineVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        sublineVersion.getMainlineSlnid());
    if (!lineVersions.isEmpty()) {
      lineVersions.sort(Comparator.comparing(LineVersion::getValidFrom));
      LineVersion firstLineVersion = lineVersions.getFirst();
      LineVersion lastLineVersion = lineVersions.getLast();
      return sublineVersion.getValidFrom().isBefore(firstLineVersion.getValidFrom())
          || sublineVersion.getValidTo().isAfter(lastLineVersion.getValidTo());
    }
    return false;
  }

}
