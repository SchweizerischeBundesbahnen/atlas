package ch.sbb.line.directory.validation;

import static java.util.Comparator.comparing;

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
    if (sublineVersions.isEmpty()) {
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
    //Make sure the lists are sorted!!
    lineVersions.sort(comparing(LineVersion::getValidFrom));
    sublineVersions.sort(comparing(SublineVersion::getValidFrom));
    //Remove overlapping version to get the right Ranges
    List<LineVersion> overlappingLineVersion = filterOverlappingVersion(lineVersions);
    List<SublineVersion> overlappingSublineVersion = filterOverlappingVersion(sublineVersions);

    boolean areSublinesInsideOfLineRange = isRangeEqual(overlappingLineVersion, overlappingSublineVersion);
    boolean hasLineGapsUncoveredBySublines = hasVersionsUncoveredUncoveredGaps(overlappingLineVersion,
        overlappingSublineVersion);
    return areSublinesInsideOfLineRange && !hasLineGapsUncoveredBySublines;
  }

  boolean hasVersionsUncoveredUncoveredGaps(List<? extends Versionable> lineVersions,
      List<? extends Versionable> sublineVersions) {
    List<DataRange> firstListDataRange = getCoveredDataRanges(lineVersions);
    List<DataRange> secondListDataRange = getCoveredDataRanges(sublineVersions);
    return !firstListDataRange.equals(secondListDataRange);
  }

  <T extends Versionable> List<T> filterOverlappingVersion(List<T> versionableList) {
    if(versionableList.size() == 1){
      return versionableList;
    }
    List<T> result = new ArrayList<>();
    LocalDate currentValidTo = versionableList.get(0).getValidTo();
    result.add(versionableList.get(0));
    for (int i = 1; i < versionableList.size(); i++) {
      if(versionableList.get(i).getValidTo().isEqual(currentValidTo) || versionableList.get(i).getValidTo().isAfter(currentValidTo)){
        currentValidTo = versionableList.get(i).getValidTo();
        result.add(versionableList.get(i));
      }
    }
    return result;
  }

  <T extends Versionable> List<DataRange> getCoveredDataRanges(List<T> versionableList) {
    List<DataRange> coveredDataRages = new ArrayList<>();
    if (versionableList.size() == 1) {
      T firstVersionable = versionableList.get(0);
      DataRange currentDataRange = new DataRange(firstVersionable.getValidFrom(),
          firstVersionable.getValidTo());
      coveredDataRages.add(currentDataRange);
      return coveredDataRages;
    }
    if (versionableList.size() >= 1) {
      T firstVersionable = versionableList.get(0);
      DataRange currentDataRange = new DataRange(firstVersionable.getValidFrom(),
          firstVersionable.getValidTo());
      for (int i = 1; i < versionableList.size(); i++) {
        T current = versionableList.get(i - 1);
        T next = versionableList.get(i);
        if ((next.getValidFrom().isBefore(current.getValidTo().plusDays(1))
            || next.getValidFrom().isEqual(current.getValidTo().plusDays(1)))) {
          currentDataRange.setTo(next.getValidTo());
          if ((versionableList.size() - 1) == i) {
            coveredDataRages.add(currentDataRange);
            return coveredDataRages;
          }
        } else {
          coveredDataRages.add(currentDataRange);
          currentDataRange = new DataRange(next.getValidFrom(), next.getValidTo());
        }
      }
    }
    return coveredDataRages;
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
  static class DataRange {

    private LocalDate from;
    private LocalDate to;
  }

}
