package ch.sbb.line.directory.validation;

import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.service.CoverageService;
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

  private final CoverageService coverageService;
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
    lineVersions.sort(Comparator.comparing(LineVersion::getValidFrom));
    List<SublineVersion> sublineVersions = sublineVersionRepository.getSublineVersionByMainlineSlnid(
        lineVersion.getSlnid());
    sublineVersions.sort(Comparator.comparing(SublineVersion::getValidFrom));
    boolean lineCompletelyCoverSublines = lineCompletelyCoverSublines(lineVersion, lineVersions,
        sublineVersions);
    boolean sublineCompletelyCoverLine = sublineCompletelyCoverLine(lineVersions, sublineVersions);
    return sublineCompletelyCoverLine && lineCompletelyCoverSublines;
  }

  private boolean sublineCompletelyCoverLine(List<LineVersion> lineVersions,
      List<SublineVersion> sublineVersions) {
    if(sublineVersions.isEmpty()){
      return true;
    }
    boolean isSublineRangeEqualToLineRange = isSublineRangeEqualToLineRange(sublineVersions,
        lineVersions);
    boolean hasSublineGapsUncoveredByLines = hasSublineGapsUncoveredByLines(sublineVersions);
    return !hasSublineGapsUncoveredByLines && isSublineRangeEqualToLineRange;
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
          getLineGapsBetweenSublines(sublineVersion, gapBetweenVersions));
    }
    return !gapsBetweenLinesAndSublines.isEmpty();
  }

  boolean hasSublineGapsUncoveredByLines(List<SublineVersion> sublineVersions) {
    List<LineVersion> lineVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        sublineVersions.get(0).getMainlineSlnid());
    lineVersions.sort(Comparator.comparing(LineVersion::getValidFrom));
    List<Gap> gapBetweenVersions = getGapBetweenSublineVersions(sublineVersions);
    List<Gap> lineGapsBetweenLines = getLineGapsBetweenLines(lineVersions.get(0), gapBetweenVersions);
    return !lineGapsBetweenLines.isEmpty();
  }

  private boolean isSublineRangeEqualToLineRange(List<SublineVersion> sublineVersions,
      List<LineVersion> lineVersions) {
    //No sublines no range error validation
    if(!lineVersions.isEmpty() && sublineVersions.isEmpty()){
      return true;
    }
    if (!lineVersions.isEmpty() && !sublineVersions.isEmpty()) {
      lineVersions.sort(Comparator.comparing(LineVersion::getValidFrom));
      LocalDate lineVersionsValidFrom = lineVersions.get(0).getValidFrom();
      LocalDate lineVersionsValidTo = lineVersions.get(lineVersions.size() - 1).getValidTo();

      sublineVersions.sort(Comparator.comparing(SublineVersion::getValidFrom));
      LocalDate sublineVersionsValidFrom = sublineVersions.get(0).getValidFrom();
      LocalDate sublineVersionValidTo = sublineVersions.get(sublineVersions.size() - 1)
                                                       .getValidTo();

      return sublineVersionsValidFrom.isEqual(lineVersionsValidFrom)
          && sublineVersionValidTo.isEqual(lineVersionsValidTo);
    }
    return false;
  }

  //TODO: generify
  List<Gap> getGapBetweenSublineVersions(List<SublineVersion> lineVersions) {
    List<Gap> linesGap = new ArrayList<>();
    for (int i = 1; i < lineVersions.size(); i++) {
      SublineVersion current = lineVersions.get(i - 1);
      SublineVersion next = lineVersions.get(i);
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

  //TODO: generify
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

  //TODO: generify
  static List<Gap> getLineGapsBetweenSublines(
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

  //TODO: generify
  static List<Gap> getLineGapsBetweenLines(
      LineVersion lineVersion, List<Gap> gaps) {
    return gaps.stream()
               .filter(
                   gap -> !gap.getFrom()
                              .isAfter(
                                  lineVersion.getValidTo()))
               .filter(
                   gap -> !gap.getTo()
                              .isBefore(
                                  lineVersion.getValidFrom()))
               .collect(
                   Collectors.toList());
  }


  private void validationComplete(LineVersion lineVersion) {
    updateLineSublineCoverage(lineVersion, true);
  }

  private void validationIncomplete(LineVersion lineVersion) {
    updateLineSublineCoverage(lineVersion, false);
  }

  private void updateLineSublineCoverage(LineVersion lineVersion, boolean isCompletelyCovered) {
    coverageService.updateSublineCoverageByLine(isCompletelyCovered, lineVersion); //complete
    List<SublineVersion> sublineVersionByMainlineSlnid = sublineVersionRepository.getSublineVersionByMainlineSlnid(
        lineVersion.getSlnid());
    for (SublineVersion sublineVersion : sublineVersionByMainlineSlnid) {
      coverageService.updateSublineCoverageBySubline(isCompletelyCovered,
          sublineVersion); //complete
    }
  }

  @Data
  @Builder
  static class Gap {

    private LocalDate from;
    private LocalDate to;
  }
}
