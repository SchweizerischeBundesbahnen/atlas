package ch.sbb.line.directory.validation;

import static java.util.Comparator.comparing;

import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.enumaration.SublineType;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.service.CoverageService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
    List<LineVersion> lineVersions = getSortedLineVersions(lineVersion);
    List<SublineVersion> sublineVersions = getSortedSublineVersions(lineVersion);
    boolean areLinesAndSublinesCompletelyCovered = areLinesAndSublinesCompletelyCovered(
        lineVersions, sublineVersions);
    if (sublineVersions.isEmpty()) {
      coverageService.coverageComplete(lineVersion, sublineVersions);
    } else if (areLinesAndSublinesCompletelyCovered) {
      coverageService.coverageComplete(lineVersion, sublineVersions);
    } else {
      coverageService.coverageIncomplete(lineVersion, sublineVersions);
    }
  }

  boolean areLinesAndSublinesCompletelyCovered(List<LineVersion> lineVersions,
      List<SublineVersion> sublineVersions) {
    if(sublineVersions.isEmpty()){
      return true;
    }
    List<SublineVersion> technincalSublines = getSublinesByType(sublineVersions,
        SublineType.TECHNICAL);
    List<SublineVersion> compensationSublines = getSublinesByType(sublineVersions,
        SublineType.COMPENSATION);

    boolean lineCompletelyCoverByTechnicalSublines = false;
    if (!technincalSublines.isEmpty()) {
      lineCompletelyCoverByTechnicalSublines = lineCompletelyCoverSublines(lineVersions,
          technincalSublines);
    }
    boolean lineCompletelyCoverByCompensationSublines = false;
    if (!compensationSublines.isEmpty()) {
      lineCompletelyCoverByCompensationSublines = lineCompletelyCoverSublines(lineVersions,
          compensationSublines);
    }

    return lineCompletelyCoverByCompensationSublines || lineCompletelyCoverByTechnicalSublines;
  }

  private List<SublineVersion> getSublinesByType(List<SublineVersion> sublineVersions,
      SublineType compensation) {
    return sublineVersions.stream()
                          .filter(s -> s.getType()
                              == compensation)
                          .collect(Collectors.toList());
  }

  private boolean lineCompletelyCoverSublines(List<LineVersion> lineVersions,
      List<SublineVersion> sublineVersions) {

    boolean areSublinesInsideOfLineRange = isRangeEqual(lineVersions, sublineVersions);
    boolean hasLineGapsUncoveredBySublines = hasVersionsUncoveredUncoveredGaps(lineVersions, sublineVersions);
    return areSublinesInsideOfLineRange && !hasLineGapsUncoveredBySublines;
  }

 boolean hasVersionsUncoveredUncoveredGaps(List<? extends Versionable> lineVersions,
      List<? extends Versionable> sublineVersions) {
    List<Gap> gapBetweenLineVersions = getGapBetweenSublineVersionable(lineVersions);
    List<Gap> gapBetweenSublineVersions = getGapBetweenSublineVersionable(sublineVersions);
    return !gapBetweenLineVersions.equals(gapBetweenSublineVersions);
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

  private List<SublineVersion> getSortedSublineVersions(LineVersion lineVersion) {
    List<SublineVersion> sublineVersions =
        sublineVersionRepository.getSublineVersionByMainlineSlnid(lineVersion.getSlnid());
    sublineVersions.sort(comparing(SublineVersion::getValidFrom));
    return sublineVersions;
  }

  private List<LineVersion> getSortedLineVersions(LineVersion lineVersion) {
    List<LineVersion> lineVersions =
        lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid());
    if (lineVersions == null || lineVersions.isEmpty()) {
      throw new IllegalStateException("At this point we must have at least one lineVersion");
    }
    lineVersions.sort(comparing(LineVersion::getValidFrom));
    return lineVersions;
  }

  //Move to DateHelper
  boolean isRangeEqual(List<? extends Versionable> firstList,
      List<? extends Versionable> secondList) {
    LocalDate startRangeFirstList = getStartRange(firstList);
    LocalDate startRangeSecondList = getStartRange(secondList);
    LocalDate endRangeFirstList = getEndRange(firstList);
    LocalDate endRangeSecondList = getEndRange(secondList);
    return startRangeFirstList.equals(startRangeSecondList) && endRangeFirstList.equals(
        endRangeSecondList);
  }

  //Move to DateHelper
  private <T extends Versionable> LocalDate getStartRange(List<T> versionableList) {
    if (versionableList.isEmpty()) {
      throw new IllegalStateException(
          "At this point we must have at least one item in the versionableList");
    }
    return versionableList.get(0).getValidFrom();
  }

  //Move to DateHelper
  private <T extends Versionable> LocalDate getEndRange(List<T> versionableList) {
    if (versionableList.isEmpty()) {
      throw new IllegalStateException(
          "At this point we must have at least one item in the versionableList");
    }
    return versionableList.get(versionableList.size() - 1).getValidTo();
  }

  @Data
  @Builder
  static class Gap {

    private LocalDate from;
    private LocalDate to;
  }

}
