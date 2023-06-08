package ch.sbb.line.directory.validation;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.line.directory.exception.LineConflictException;
import ch.sbb.line.directory.exception.TemporaryLineValidationException;
import ch.sbb.line.directory.repository.LineVersionRepository;
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

  private final LineVersionRepository lineVersionRepository;
  private final CoverageValidationService coverageValidationService;
  private final SharedBusinessOrganisationService sharedBusinessOrganisationService;

  public void validateLinePreconditionBusinessRule(LineVersion lineVersion) {
    validateLineConflict(lineVersion);
    sharedBusinessOrganisationService.validateSboidExists(lineVersion.getBusinessOrganisation());
  }

  public void validateLineAfterVersioningBusinessRule(LineVersion lineVersion) {
    validateTemporaryLinesDuration(lineVersion);
    coverageValidationService.validateLineSublineCoverage(lineVersion);
  }

  void validateLineConflict(LineVersion lineVersion) {
    List<LineVersion> swissLineNumberOverlaps = lineVersionRepository.findSwissLineNumberOverlaps(
        lineVersion);
    if (!swissLineNumberOverlaps.isEmpty()) {
      throw new LineConflictException(lineVersion, swissLineNumberOverlaps);
    }
  }

  void validateTemporaryLinesDuration(LineVersion lineVersion) {
    if (LineType.TEMPORARY.equals(lineVersion.getLineType())) {
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
                             .filter(version -> LineType.TEMPORARY.equals(version.getLineType())
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
