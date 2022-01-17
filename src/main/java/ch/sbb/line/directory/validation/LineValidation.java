package ch.sbb.line.directory.validation;

import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.exception.ConflictExcpetion;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class LineValidation {

  private static final int DAYS_OF_YEAR = 365;

  public void validateTemporaryLinesDuration(LineVersion lineVersion, List<LineVersion> allVersions) {
    if (getDaysBetween(lineVersion.getValidFrom(), lineVersion.getValidTo()) > DAYS_OF_YEAR) {
      throw new ConflictExcpetion(ConflictExcpetion.TEMPORARY_LINE_VALIDITY_TOO_LONG_MESSAGE);
    }
    if (allVersions.isEmpty()) {
      return;
    }
    allVersions = allVersions.stream().filter(version -> LineType.TEMPORARY.equals(version.getType()) && !Objects.equals(lineVersion.getId(), version.getId()))
        .collect(Collectors.toList());

    SortedSet<LineVersion> relatedVersions = new TreeSet<>(Comparator.comparing(LineVersion::getValidFrom));
    relatedVersions.add(lineVersion);

    List<LineVersion> versionsWhichRelate;
    do {
      versionsWhichRelate = getRelatedVersions(relatedVersions, allVersions);
      relatedVersions.addAll(versionsWhichRelate);
      allVersions.removeAll(versionsWhichRelate);
    } while (!versionsWhichRelate.isEmpty());

    if (getDaysBetween(relatedVersions.first().getValidFrom(),
        relatedVersions.last().getValidTo()) > DAYS_OF_YEAR) {
      throw new ConflictExcpetion(ConflictExcpetion.TEMPORARY_LINE_VALIDITY_TOO_LONG_MESSAGE);
    }
  }

  private List<LineVersion> getRelatedVersions(SortedSet<LineVersion> relatedVersions, List<LineVersion> allTemporaryVersions) {
    return allTemporaryVersions.stream().filter(version -> areDatesRelated(version.getValidTo(), relatedVersions.first().getValidFrom())
            || areDatesRelated(version.getValidFrom(), relatedVersions.last().getValidTo()))
        .collect(Collectors.toList());
  }

  private long getDaysBetween(LocalDate date1, LocalDate date2) {
    return Math.abs(ChronoUnit.DAYS.between(date1, date2));
  }

  private boolean areDatesRelated(LocalDate date1, LocalDate date2) {
    return Math.abs(ChronoUnit.DAYS.between(date1, date2)) == 1;
  }

}
