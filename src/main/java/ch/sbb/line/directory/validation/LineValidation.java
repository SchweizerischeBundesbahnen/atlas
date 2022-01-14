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

  public void validateTemporaryLines(LineVersion lineVersion, List<LineVersion> allVersions) {
    if (getDaysBetween(lineVersion.getValidFrom(), lineVersion.getValidTo()) > 365) {
      throw new ConflictExcpetion(ConflictExcpetion.TEMPORARY_LINE_VALIDITY_TOO_LONG_MESSAGE);
    }
    if (allVersions.isEmpty()) {
      return;
    }
    allVersions = allVersions.stream().filter(version -> LineType.TEMPORARY.equals(version.getType()) && !Objects.equals(lineVersion.getId(), version.getId()))
        .collect(Collectors.toList());

    SortedSet<LineVersion> contiguousVersions = new TreeSet<>(Comparator.comparing(LineVersion::getValidFrom));
    contiguousVersions.add(lineVersion);

    List<LineVersion> versionsWhichContiguou;
    do {
      versionsWhichContiguou = getContiguousVersions(contiguousVersions, allVersions);
      contiguousVersions.addAll(versionsWhichContiguou);
      allVersions.removeAll(versionsWhichContiguou);
    } while (!versionsWhichContiguou.isEmpty());

    if (getDaysBetween(contiguousVersions.first().getValidFrom(),
        contiguousVersions.last().getValidTo()) > 365) {
      throw new ConflictExcpetion(ConflictExcpetion.TEMPORARY_LINE_VALIDITY_TOO_LONG_MESSAGE);
    }
  }

  private List<LineVersion> getContiguousVersions(SortedSet<LineVersion> contiguousVersions, List<LineVersion> allTemporaryVersions) {
    return allTemporaryVersions.stream().filter(version -> areDatesContiguous(version.getValidTo(), contiguousVersions.first().getValidFrom())
            || areDatesContiguous(version.getValidFrom(), contiguousVersions.last().getValidTo()))
        .collect(Collectors.toList());
  }

  private long getDaysBetween(LocalDate date1, LocalDate date2) {
    return Math.abs(ChronoUnit.DAYS.between(date1, date2));
  }

  private boolean areDatesContiguous(LocalDate date1, LocalDate date2) {
    return Math.abs(ChronoUnit.DAYS.between(date1, date2)) == 1;
  }

}
