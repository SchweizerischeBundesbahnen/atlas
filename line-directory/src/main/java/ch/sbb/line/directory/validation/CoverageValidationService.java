package ch.sbb.line.directory.validation;

import static ch.sbb.line.directory.enumaration.SublineType.COMPENSATION;
import static ch.sbb.line.directory.enumaration.SublineType.TECHNICAL;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

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
    boolean areLinesAndSublinesCompletelyCovered =
        areLinesAndSublinesCompletelyCovered(lineVersions, sublineVersions);
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
    List<SublineVersion> technincalSublines = getSublinesByType(sublineVersions, TECHNICAL);
    List<SublineVersion> compensationSublines = getSublinesByType(sublineVersions, COMPENSATION);

    boolean lineCompletelyCoverByTechnicalSublines =
        isLineCompletelyCoveredBySublines(lineVersions, technincalSublines);
    boolean lineCompletelyCoverByCompensationSublines =
        isLineCompletelyCoveredBySublines(lineVersions, compensationSublines);

    return lineCompletelyCoverByCompensationSublines || lineCompletelyCoverByTechnicalSublines;
  }

  private boolean isLineCompletelyCoveredBySublines(List<LineVersion> lineVersions,
      List<SublineVersion> sublinesVersions) {
    boolean lineCompletelyCoverByTechnicalSublines = false;
    if (!sublinesVersions.isEmpty()) {
      lineCompletelyCoverByTechnicalSublines = lineCompletelyCoverSublines(lineVersions,
          sublinesVersions);
    }
    return lineCompletelyCoverByTechnicalSublines;
  }

  private List<SublineVersion> getSublinesByType(List<SublineVersion> sublineVersions,
      SublineType sublineType) {
    return sublineVersions.stream().filter(s -> s.getType() == sublineType).collect(toList());
  }

  private boolean lineCompletelyCoverSublines(List<LineVersion> lineVersions,
      List<SublineVersion> sublineVersions) {

    //Remove overlapped versions to get the right Ranges
    List<LineVersion> filteredLineVersions = removeOverlappedVersion(lineVersions);
    List<SublineVersion> filteredSublineVersions = removeOverlappedVersion(sublineVersions);

    boolean areSublinesInsideOfLineRange =
        isRangeEqual(filteredLineVersions, filteredSublineVersions);
    boolean hasLineGapsUncoveredBySublines =
        hasVersionsUncoveredUncoveredGaps(filteredLineVersions, filteredSublineVersions);
    return areSublinesInsideOfLineRange && !hasLineGapsUncoveredBySublines;
  }

  boolean hasVersionsUncoveredUncoveredGaps(List<? extends Versionable> lineVersions,
      List<? extends Versionable> sublineVersions) {
    List<DataRange> firstListDataRange = getCoveredDataRanges(lineVersions);
    List<DataRange> secondListDataRange = getCoveredDataRanges(sublineVersions);
    return !firstListDataRange.equals(secondListDataRange);
  }

  private <T extends Versionable> List<DataRange> getDataRangeFromSingleVersion(
      List<T> versionableList, List<DataRange> coveredDataRages) {
    T firstVersionable = versionableList.get(0);
    DataRange currentDataRange = new DataRange(firstVersionable.getValidFrom(),
        firstVersionable.getValidTo());
    coveredDataRages.add(currentDataRange);
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

  <T extends Versionable> List<T> removeOverlappedVersion(List<T> versionableList) {
    if (versionableList.size() == 1) {
      return versionableList;
    }
    versionableList.sort(comparing(T::getValidFrom)); //Make sure the list is sorted!!
    List<T> result = new ArrayList<>();
    LocalDate currentValidTo = versionableList.get(0).getValidTo();
    result.add(versionableList.get(0));
    for (int i = 1; i < versionableList.size(); i++) {
      T actual = versionableList.get(i);
      if (actual.getValidTo().isEqual(currentValidTo)
          || actual.getValidTo().isAfter(currentValidTo)) {
        currentValidTo = actual.getValidTo();
        result.add(actual);
      }
    }
    return result;
  }

  <T extends Versionable> List<DataRange> getCoveredDataRanges(List<T> versionableList) {
    List<DataRange> coveredDataRages = new ArrayList<>();
    if (versionableList.size() == 1) {
      return getDataRangeFromSingleVersion(versionableList, coveredDataRages);
    }
    if (versionableList.size() > 1) {
      T firstItem = versionableList.get(0);
      DataRange currentDataRange = new DataRange(firstItem.getValidFrom(), firstItem.getValidTo());
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
