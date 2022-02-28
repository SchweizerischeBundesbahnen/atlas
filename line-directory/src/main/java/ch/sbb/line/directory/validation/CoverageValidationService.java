package ch.sbb.line.directory.validation;

import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.service.SublineCoverageService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CoverageValidationService {

  private final SublineCoverageService sublineCoverageService;
  private final SublineVersionRepository sublineVersionRepository;
  private final LineVersionRepository lineVersionRepository;

  public void validateLineSublineCoverage(LineVersion lineVersion) {
    boolean areLinesAndSublinesCompletelyCovered = areLinesAndSublinesCompletelyCovered(
        lineVersion);
    if (areLinesAndSublinesCompletelyCovered) {
      validationComplete(lineVersion);
    } else {
      validationIncomplete(lineVersion);
    }
  }

  boolean areLinesAndSublinesCompletelyCovered(LineVersion lineVersion) {
    List<LineVersion> lineVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        lineVersion.getSlnid());
    List<SublineVersion> sublineVersions = sublineVersionRepository.getSublineVersionByMainlineSlnid(
        lineVersion.getSlnid());
    boolean lineCompletelyCoverSublines = lineCompletelyCoverSublines(lineVersion, lineVersions,
        sublineVersions);
    boolean sublineCompletelyCoverLine = sublineCompletelyCoverLine(lineVersions, sublineVersions);
    return sublineCompletelyCoverLine && lineCompletelyCoverSublines;
  }

  private boolean sublineCompletelyCoverLine(List<LineVersion> lineVersions,
      List<SublineVersion> sublineVersions) {
    boolean hasSublineGapsUncoveredByLines = hasSublineGapsUncoveredByLines(sublineVersions);
    boolean isSublineRangeOutsideOfLineRange = isSublineRangeOutsideOfLineRange(sublineVersions,
        lineVersions);
    return !hasSublineGapsUncoveredByLines && !isSublineRangeOutsideOfLineRange;
  }

  private boolean lineCompletelyCoverSublines(LineVersion lineVersion,
      List<LineVersion> lineVersions,
      List<SublineVersion> sublineVersions) {
    //The Line range must cover the entire sublines range
    boolean areSublinesInsideOfLineRange = areSublinesInsideOfLineRange(lineVersion, lineVersions,
        sublineVersions);
    //The Line cannot have any gap that is not covered by sublines
    boolean hasLineGapsUncoveredBySublines = hasLineGapsUncoveredBySublines(lineVersions,
        sublineVersions);
    return areSublinesInsideOfLineRange && !hasLineGapsUncoveredBySublines;
  }

  boolean areSublinesInsideOfLineRange(LineVersion lineVersion,
      List<LineVersion> lineVersions,
      List<SublineVersion> sublineVersions) {
    LocalDate lineValidFrom = lineVersion.getValidFrom();
    LocalDate lineValidTo = lineVersion.getValidTo();
    if (!lineVersions.isEmpty()) {
      lineValidFrom = lineVersions.get(0).getValidFrom();
      lineValidTo = lineVersions.get(lineVersions.size() - 1).getValidTo();
    }
    sublineVersions.sort(Comparator.comparing(SublineVersion::getValidFrom));

    if (!sublineVersions.isEmpty()) {
      LocalDate firstSublineVersionValidFrom = sublineVersions.get(0).getValidFrom();
      LocalDate lastSublineVersionValidTo = sublineVersions.get(sublineVersions.size() - 1)
                                                           .getValidTo();
      return lineValidFrom.isEqual(firstSublineVersionValidFrom) && lineValidTo.isEqual(
          lastSublineVersionValidTo) && haveSublineTheSameType(sublineVersions);
    }
    return true;
  }

  boolean haveSublineTheSameType(List<SublineVersion> sublineVersions) {
    long count = IntStream
        .range(0, sublineVersions.size() - 1)
        .filter(i -> sublineVersions.get(i).getType() != sublineVersions.get(i + 1).getType())
        .count();
    return count == 0;
  }

  boolean hasLineGapsUncoveredBySublines(List<LineVersion> lineVersions,
      List<SublineVersion> sublineVersions) {
    List<Gap> gapBetweenVersions = getGapBetweenVersions(lineVersions);
    List<Gap> gapsBetweenLinesAndSublines = new ArrayList<>();
    for (SublineVersion sublineVersion : sublineVersions) {
      gapsBetweenLinesAndSublines.addAll(
          getLineGapsBetweenLines(sublineVersion, gapBetweenVersions));
    }
    return !gapsBetweenLinesAndSublines.isEmpty();
  }

  boolean hasSublineGapsUncoveredByLines(List<SublineVersion> sublineVersions) {
    for (SublineVersion sublineVersion : sublineVersions) {
      boolean hasSublineUncoveredGapOnTheLine = hasSublineUncoveredGapOnTheLine(sublineVersion);
      if (hasSublineUncoveredGapOnTheLine) {
        return true;
      }
    }
    return false;
  }

  boolean hasSublineUncoveredGapOnTheLine(SublineVersion sublineVersion) {
    List<LineVersion> lineVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        sublineVersion.getMainlineSlnid());
    List<Gap> gapBetweenVersions = getGapBetweenVersions(lineVersions);
    List<Gap> lineGapsBetweenLines = getLineGapsBetweenLines(sublineVersion, gapBetweenVersions);
    return !lineGapsBetweenLines.isEmpty();
  }

  private boolean isSublineRangeOutsideOfLineRange(List<SublineVersion> sublineVersions,
      List<LineVersion> lineVersions) {

    if (!lineVersions.isEmpty() && !sublineVersions.isEmpty()) {
      lineVersions.sort(Comparator.comparing(LineVersion::getValidFrom));
      LineVersion firstLineVersion = lineVersions.get(0);
      LineVersion lastLineVersion = lineVersions.get(lineVersions.size() - 1);

      sublineVersions.sort(Comparator.comparing(SublineVersion::getValidFrom));
      SublineVersion firstSublineVersion = sublineVersions.get(0);
      SublineVersion lastSublineVersion = sublineVersions.get(sublineVersions.size() - 1);

      return firstSublineVersion.getValidFrom().isBefore(firstLineVersion.getValidFrom())
          || lastSublineVersion.getValidTo().isAfter(lastLineVersion.getValidTo());
    }
    return false;
  }

  List<Gap> getGapBetweenVersions(List<LineVersion> lineVersions) {
    List<Gap> linesGap = new ArrayList<>();
    for (int i = 1; i < lineVersions.size(); i++) {
      LineVersion current = lineVersions.get(i - 1);
      LineVersion next = lineVersions.get(i);
      if (!DateHelper.areDatesSequential(current.getValidTo(),
          next.getValidFrom())) {
        Gap gap = Gap.builder()
                     .from(current.getValidTo().plusDays(1))
                     .to(next.getValidFrom().minusDays(1))
                     .build();
        linesGap.add(gap);
      }
    }
    return linesGap;
  }

  static List<Gap> getLineGapsBetweenLines(
      SublineVersion sublineVersion, List<Gap> gaps) {
    return gaps.stream()
               .filter(
                   gap -> !gap.getFrom()
                              .isAfter(
                                  sublineVersion.getValidTo()))
               .filter(
                   gap -> !gap.getTo()
                              .isBefore(
                                  sublineVersion.getValidFrom()))
               .collect(
                   Collectors.toList());
  }


  private void validationComplete(LineVersion lineVersion) {
    updateValidationSublineCoverage(lineVersion, true);
  }

  private void validationIncomplete(LineVersion lineVersion) {
    updateValidationSublineCoverage(lineVersion, false);
  }

  private void updateValidationSublineCoverage(LineVersion lineVersion, boolean b) {
    sublineCoverageService.updateSublineCoverageByLine(b, lineVersion); //complete
    List<SublineVersion> sublineVersionByMainlineSlnid = sublineVersionRepository.getSublineVersionByMainlineSlnid(
        lineVersion.getSlnid());
    for (SublineVersion sublineVersion : sublineVersionByMainlineSlnid) {
      sublineCoverageService.updateSublineCoverageBySubline(b, sublineVersion); //complete
    }
  }

  @Data
  @Builder
  static class Gap {

    private LocalDate from;
    private LocalDate to;
  }
}
