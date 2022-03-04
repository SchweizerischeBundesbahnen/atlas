package ch.sbb.line.directory.validation;

import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.exception.SubLineAssignToLineConflictException;
import ch.sbb.line.directory.exception.SublineConflictException;
import ch.sbb.line.directory.exception.SublineOutsideOfLineRangeException;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SublineValidationService {

  private final SublineVersionRepository sublineVersionRepository;
  private final LineVersionRepository lineVersionRepository;

  public void validateSublineBusinessRules(SublineVersion sublineVersion) {
    validateSublineConflict(sublineVersion);
    validateLineRangeRule(sublineVersion);
    validateDifferentMainLineAssignRule(sublineVersion);
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

  void validateLineRangeRule(SublineVersion sublineVersion) {
    List<LineVersion> lineVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        sublineVersion.getMainlineSlnid());
    if (!lineVersions.isEmpty()) {
      lineVersions.sort(Comparator.comparing(LineVersion::getValidFrom));
      LineVersion firstLineVersion = lineVersions.get(0);
      LineVersion lastLineVersion = lineVersions.get(lineVersions.size() - 1);
      if (sublineVersion.getValidFrom().isBefore(firstLineVersion.getValidFrom())
          || sublineVersion.getValidTo().isAfter(lastLineVersion.getValidTo())) {
        throw new SublineOutsideOfLineRangeException(sublineVersion,
            firstLineVersion.getSwissLineNumber(), firstLineVersion.getValidFrom(),
            lastLineVersion.getValidTo());
      }
    }
  }

}
