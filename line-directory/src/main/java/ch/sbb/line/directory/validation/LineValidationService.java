package ch.sbb.line.directory.validation;

import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.exception.LineConflictException;
import ch.sbb.line.directory.exception.TemporaryLineValidationException;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class LineValidationService {

  private static final int DAYS_OF_YEAR = 365;

  private final SublineVersionRepository sublineVersionRepository;
  private final LineVersionRepository lineVersionRepository;
  private final SublineCoverageValidationService sublineCoverageValidationService;

  public void validateLinePreconditionBusinessRule(LineVersion lineVersion) {
    validateLineConflict(lineVersion);
  }

  public void validateLineAfterVersioningBusinessRule(LineVersion lineVersion) {
    validateTemporaryLinesDuration(lineVersion);
    sublineCoverageValidationService.validateSublineRangeOutsideOfLineRange(lineVersion);
//    boolean validationIssueResult = validateLineRangeOutsideOfLineRange(lineVersion);
//    sublineCoverageService.updateSublineCoverageByLine(validationIssueResult, lineVersion);
  }

  void validateLineConflict(LineVersion lineVersion) {
    List<LineVersion> swissLineNumberOverlaps = lineVersionRepository.findSwissLineNumberOverlaps(
        lineVersion);
    if (!swissLineNumberOverlaps.isEmpty()) {
      throw new LineConflictException(lineVersion, swissLineNumberOverlaps);
    }
  }

  void validateTemporaryLinesDuration(LineVersion lineVersion) {
    if (LineType.TEMPORARY.equals(lineVersion.getType())) {
      List<LineVersion> allBySlnidOrderByValidFrom = lineVersionRepository.findAllBySlnidOrderByValidFrom(
          lineVersion.getSlnid());
      doValidateTemporaryLinesDuration(lineVersion, allBySlnidOrderByValidFrom);
    }
  }

  void doValidateTemporaryLinesDuration(LineVersion lineVersion,
      List<LineVersion> allVersions) {
    if (getDaysBetween(lineVersion.getValidFrom(), lineVersion.getValidTo()) > DAYS_OF_YEAR) {
      throw new TemporaryLineValidationException(List.of(lineVersion));
    }
    if (allVersions.isEmpty()) {
      return;
    }
    allVersions = allVersions.stream()
                             .filter(version -> LineType.TEMPORARY.equals(version.getType())
                                 && !Objects.equals(lineVersion.getId(), version.getId()))
                             .collect(Collectors.toList());

    SortedSet<LineVersion> relatedVersions = new TreeSet<>(
        Comparator.comparing(LineVersion::getValidFrom));
    relatedVersions.add(lineVersion);

    List<LineVersion> versionsWhichRelate;
    do {
      versionsWhichRelate = getRelatedVersions(relatedVersions, allVersions);
      relatedVersions.addAll(versionsWhichRelate);
      allVersions.removeAll(versionsWhichRelate);
    } while (!versionsWhichRelate.isEmpty());

    if (getDaysBetween(relatedVersions.first().getValidFrom(),
        relatedVersions.last().getValidTo()) > DAYS_OF_YEAR) {
      throw new TemporaryLineValidationException(new ArrayList<>(relatedVersions));
    }
  }

  boolean validateLineRangeOutsideOfLineRange(LineVersion lineVersion) {
    List<LineVersion> lineVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        lineVersion.getSlnid());
    LocalDate lineValidFrom = lineVersion.getValidFrom();
    LocalDate lineValidTo = lineVersion.getValidTo();
    if (!lineVersions.isEmpty()) {
      lineValidFrom = lineVersions.get(0).getValidFrom();
      lineValidTo = lineVersions.get(lineVersions.size() - 1).getValidTo();
    }

    List<SublineVersion> sublineVersions = sublineVersionRepository.getSublineVersionByMainlineSlnid(
        lineVersion.getSlnid());
    sublineVersions.sort(Comparator.comparing(SublineVersion::getValidFrom));
    if (!sublineVersions.isEmpty()) {
      SublineVersion firstSublineVersion = sublineVersions.get(0);
      SublineVersion lastSublineVersion = sublineVersions.get(sublineVersions.size() - 1);
      return lineValidFrom.isAfter(firstSublineVersion.getValidFrom())
          || lineValidTo.isBefore(lastSublineVersion.getValidTo());
    }
    return false;
  }

  private List<LineVersion> getRelatedVersions(SortedSet<LineVersion> relatedVersions,
      List<LineVersion> allTemporaryVersions) {
    return allTemporaryVersions.stream()
                               .filter(version -> areDatesRelated(version.getValidTo(),
                                   relatedVersions.first().getValidFrom())
                                   || areDatesRelated(version.getValidFrom(),
                                   relatedVersions.last().getValidTo()))
                               .collect(Collectors.toList());
  }

  private long getDaysBetween(LocalDate date1, LocalDate date2) {
    return Math.abs(ChronoUnit.DAYS.between(date1, date2));
  }

  private boolean areDatesRelated(LocalDate date1, LocalDate date2) {
    return getDaysBetween(date1, date2) == 1;
  }

}
