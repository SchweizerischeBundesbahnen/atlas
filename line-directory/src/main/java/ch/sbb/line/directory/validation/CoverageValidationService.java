package ch.sbb.line.directory.validation;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
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
    List<Boolean> validationResult = new ArrayList<>();

    for (SublineType sublineType : SublineType.values()) {
      List<SublineVersion> sublinesByType = getSublinesByType(sublineVersions, sublineType);
      boolean result = isLineCompletelyCoveredBySublines(lineVersions, sublinesByType);
      validationResult.add(result);
    }
    long falseResultSize = validationResult.stream().filter(vr -> !vr).count();
    return falseResultSize <= 0;
  }

  private boolean isLineCompletelyCoveredBySublines(List<LineVersion> lineVersions,
      List<SublineVersion> sublinesVersions) {
    boolean lineCompletelyCoverByTechnicalSublines;
    if (!sublinesVersions.isEmpty()) {
      lineCompletelyCoverByTechnicalSublines = lineCompletelyCoverSublines(lineVersions,
          sublinesVersions);
    } else {
      return true;
    }
    return lineCompletelyCoverByTechnicalSublines;
  }

  private List<SublineVersion> getSublinesByType(List<SublineVersion> sublineVersions,
      SublineType sublineType) {
    return sublineVersions.stream()
                          .filter(s -> s.getSublineType() == sublineType)
                          .collect(toList());
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
    List<DateRange> firstListDateRange = getCoveredDataRanges(lineVersions);
    List<DateRange> secondListDateRange = getCoveredDataRanges(sublineVersions);
    return !firstListDateRange.equals(secondListDateRange);
  }

  private <T extends Versionable> List<DateRange> getDataRangeFromSingleVersion(
      List<T> versionableList, List<DateRange> coveredDataRages) {
    T firstVersionable = versionableList.get(0);
    DateRange currentDateRange = new DateRange(firstVersionable.getValidFrom(),
        firstVersionable.getValidTo());
    coveredDataRages.add(currentDateRange);
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

  <T extends Versionable> List<DateRange> getCoveredDataRanges(List<T> versionableList) {
    List<DateRange> coveredDataRages = new ArrayList<>();
    if (versionableList.size() == 1) {
      return getDataRangeFromSingleVersion(versionableList, coveredDataRages);
    }
    if (versionableList.size() > 1) {
      T firstItem = versionableList.get(0);
      DateRange currentDateRange = new DateRange(firstItem.getValidFrom(), firstItem.getValidTo());
      for (int i = 1; i <= versionableList.size(); i++) {
        T current = versionableList.get(i - 1);
        if ((versionableList.size()) == i) {
          if (coveredDataRages.get(coveredDataRages.size() - 1)
                              .getTo()
                              .plusDays(1)
                              .isEqual(current.getValidFrom())) {
            coveredDataRages.get(coveredDataRages.size() - 1).setTo(current.getValidTo());
            return coveredDataRages;
          }
          coveredDataRages.add(currentDateRange);
          return coveredDataRages;
        }
        T next = versionableList.get(i);
        if ((next.getValidFrom().isBefore(current.getValidTo().plusDays(1))
            || next.getValidFrom().isEqual(current.getValidTo().plusDays(1)))) {
          currentDateRange.setTo(next.getValidTo());
          if ((versionableList.size() - 1) == i) {
            coveredDataRages.add(currentDateRange);
            return coveredDataRages;
          }
        } else {
          coveredDataRages.add(currentDateRange);
          currentDateRange = new DateRange(next.getValidFrom(), next.getValidTo());
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
  static class DateRange {

    private LocalDate from;
    private LocalDate to;
  }

}
