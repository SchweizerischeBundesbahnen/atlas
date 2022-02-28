package ch.sbb.line.directory.validation;

import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.exception.SubLineAssignToLineConflictException;
import ch.sbb.line.directory.exception.SublineConflictException;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.service.SublineCoverageService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SublineValidationService {

  private final SublineVersionRepository sublineVersionRepository;
  private final LineVersionRepository lineVersionRepository;
  private final SublineCoverageService sublineCoverageService;

  public void validatePreconditionSublineBusinessRules(SublineVersion sublineVersion) {
    validateSublineConflict(sublineVersion);
    validateDifferentMainLineAssignRule(sublineVersion);
  }

  public void validateSublineAfterVersioningBusinessRule(SublineVersion sublineVersion) {
    boolean validationLineRangeRuleResult = validateLineRangeRule(sublineVersion);
    sublineCoverageService.updateSublineCoverageBySubline(validationLineRangeRuleResult,
        sublineVersion);
  }

  void validateSublineConflict(SublineVersion sublineVersion) {
    List<SublineVersion> swissLineNumberOverlaps = sublineVersionRepository.findSwissLineNumberOverlaps(
        sublineVersion);
    if (!swissLineNumberOverlaps.isEmpty()) {
      throw new SublineConflictException(sublineVersion, swissLineNumberOverlaps);
    }
  }

  void validateDifferentMainLineAssignRule(SublineVersion sublineVersion) {
    if (sublineVersion.getId() != null) {
      SublineVersion sublineVersionActual =
          sublineVersionRepository.findById(sublineVersion.getId())
                                  .orElse(null);
      if (sublineVersionActual != null &&
          !sublineVersionActual.getMainlineSlnid()
                               .equals(sublineVersion.getMainlineSlnid())) {
        throw new SubLineAssignToLineConflictException(sublineVersionActual);
      }
    }
  }

  boolean validateLineRangeRule(SublineVersion sublineVersion) {
    List<LineVersion> lineVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        sublineVersion.getMainlineSlnid());
    if (!lineVersions.isEmpty()) {
      lineVersions.sort(Comparator.comparing(LineVersion::getValidFrom));
      LineVersion firstLineVersion = lineVersions.get(0);
      LineVersion lastLineVersion = lineVersions.get(lineVersions.size() - 1);
      return sublineVersion.getValidFrom().isBefore(firstLineVersion.getValidFrom())
          || sublineVersion.getValidTo().isAfter(lastLineVersion.getValidTo());
    }
    return false;
  }

}
