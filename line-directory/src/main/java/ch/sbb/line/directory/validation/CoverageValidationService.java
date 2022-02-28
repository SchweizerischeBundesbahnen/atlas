package ch.sbb.line.directory.validation;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.service.CoverageService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    List<LineVersion> lineVersions = getSortedLineVersions(lineVersion);
    List<SublineVersion> sublineVersions = getSortedSublineVersions(lineVersion);

    boolean lineCompletelyCoverSublines =
        lineCompletelyCoverSublines(lineVersions, sublineVersions);
    boolean sublineCompletelyCoverLine = sublineCompletelyCoverLine(lineVersions, sublineVersions);
    return sublineCompletelyCoverLine && lineCompletelyCoverSublines;
  }

  private boolean sublineCompletelyCoverLine(List<LineVersion> lineVersions,
      List<SublineVersion> sublineVersions) {
    if (sublineVersions.isEmpty()) {
      return true;
    }
    boolean isSublineRangeEqualToLineRange = isSublineRangeEqualToLineRange(sublineVersions,
        lineVersions);
    boolean hasSublineGapsUncoveredByLines = hasSublineGapsUncoveredByLines(sublineVersions);
    return !hasSublineGapsUncoveredByLines && isSublineRangeEqualToLineRange;
  }

  private boolean lineCompletelyCoverSublines(List<LineVersion> lineVersions,
      List<SublineVersion> sublineVersions) {

    boolean areSublinesInsideOfLineRange =
        areSublinesInsideOfLineRange(lineVersions, sublineVersions);
    boolean hasLineGapsUncoveredBySublines =
        hasLineGapsUncoveredBySublines(lineVersions, sublineVersions);
    return areSublinesInsideOfLineRange && !hasLineGapsUncoveredBySublines;
  }

  boolean areSublinesInsideOfLineRange(List<LineVersion> lineVersions, List<SublineVersion> sublineVersions) {
    LocalDate lineValidFrom = lineVersions.get(0).getValidFrom();
    LocalDate lineValidTo = lineVersions.get(lineVersions.size() - 1).getValidTo();
    if (!sublineVersions.isEmpty()) {
      LocalDate sublineValidFrom = sublineVersions.get(0).getValidFrom();
      LocalDate sublineValidTo = sublineVersions.get(sublineVersions.size() - 1).getValidTo();
      return lineValidFrom.isEqual(sublineValidFrom)
          && lineValidTo.isEqual(sublineValidTo)
          && haveSublineTheSameType(sublineVersions);
    }
    return true;
  }

  boolean haveSublineTheSameType(List<SublineVersion> sublineVersions) {
    long differentSublineTypeCount = IntStream
        .range(0, sublineVersions.size() - 1)
        .filter(i -> sublineVersions.get(i).getType() != sublineVersions.get(i + 1).getType())
        .count();
    return differentSublineTypeCount == 0;
  }

  boolean hasLineGapsUncoveredBySublines(List<LineVersion> lineVersions,
      List<SublineVersion> sublineVersions) {
    List<Gap> gapBetweenVersions = getGapBetweenSublineVersionable(lineVersions);
    List<Gap> gapsBetweenLinesAndSublines = new ArrayList<>();
    for (SublineVersion sublineVersion : sublineVersions) {
      gapsBetweenLinesAndSublines.addAll(
          getLineGapsBetweenVersionable(sublineVersion, gapBetweenVersions));
    }
    return !gapsBetweenLinesAndSublines.isEmpty();
  }

  boolean hasSublineGapsUncoveredByLines(List<SublineVersion> sublineVersions) {
    List<LineVersion> lineVersions = getSortedLineVersionsBySublines(sublineVersions);

    List<Gap> gapBetweenVersions = getGapBetweenSublineVersionable(sublineVersions);
    List<Gap> lineGapsBetweenLines = getLineGapsBetweenVersionable(lineVersions.get(0),
        gapBetweenVersions);
    return !lineGapsBetweenLines.isEmpty();
  }

  private boolean isSublineRangeEqualToLineRange(List<SublineVersion> sublineVersions,
      List<LineVersion> lineVersions) {
    //No sublines no range error validation
    if (!lineVersions.isEmpty() && sublineVersions.isEmpty()) {
      return true;
    }
    if (!lineVersions.isEmpty() && !sublineVersions.isEmpty()) {
      LocalDate lineVersionsValidFrom = lineVersions.get(0).getValidFrom();
      LocalDate lineVersionsValidTo = lineVersions.get(lineVersions.size() - 1).getValidTo();

      LocalDate sublineVersionsValidFrom = sublineVersions.get(0).getValidFrom();
      LocalDate sublineVersionValidTo = sublineVersions.get(sublineVersions.size() - 1)
                                                       .getValidTo();

      return sublineVersionsValidFrom.isEqual(lineVersionsValidFrom)
          && sublineVersionValidTo.isEqual(lineVersionsValidTo);
    }
    return false;
  }

  <T extends Versionable> List<Gap> getGapBetweenSublineVersionable(List<T> versionableList) {
    List<Gap> linesGap = new ArrayList<>();
    for (int i = 1; i < versionableList.size(); i++) {
      T current = versionableList.get(i - 1);
      T next = versionableList.get(i);
      if (!DateHelper.areDatesSequential(current.getValidTo(), next.getValidFrom())) {
        Gap gap = Gap.builder()
                     .from(current.getValidTo().plusDays(1))
                     .to(next.getValidFrom().minusDays(1))
                     .build();
        linesGap.add(gap);
      }
    }
    return linesGap;
  }

  static <T extends Versionable> List<Gap> getLineGapsBetweenVersionable(T versionable,
      List<Gap> gaps) {
    return gaps.stream()
               .filter(gap -> !gap.getFrom().isAfter(versionable.getValidTo()))
               .filter(gap -> !gap.getTo().isBefore(versionable.getValidFrom()))
               .collect(toList());
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
      coverageService.updateSublineCoverageBySubline(isCompletelyCovered, sublineVersion);
    }
  }

  private List<SublineVersion> getSortedSublineVersions(LineVersion lineVersion) {
    List<SublineVersion> sublineVersions =
        sublineVersionRepository.getSublineVersionByMainlineSlnid(lineVersion.getSlnid());
    sublineVersions.sort(comparing(SublineVersion::getValidFrom));
    return sublineVersions;
  }

  private List<LineVersion> getSortedLineVersions(LineVersion lineVersion) {
    List<LineVersion> lineVersions =
        lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid());
    if(lineVersions == null || lineVersions.isEmpty()){
      throw new IllegalStateException("At this point we must have at least one lineVersion");
    }
    lineVersions.sort(comparing(LineVersion::getValidFrom));
    return lineVersions;
  }

  private List<LineVersion> getSortedLineVersionsBySublines(List<SublineVersion> sublineVersions) {
    if(sublineVersions.isEmpty()){
      throw new IllegalStateException("At this point we must have at least one sublineVersion");
    }
    List<LineVersion> lineVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        sublineVersions.get(0).getMainlineSlnid());
    lineVersions.sort(comparing(LineVersion::getValidFrom));
    return lineVersions;
  }

  @Data
  @Builder
  static class Gap {

    private LocalDate from;
    private LocalDate to;
  }
}
