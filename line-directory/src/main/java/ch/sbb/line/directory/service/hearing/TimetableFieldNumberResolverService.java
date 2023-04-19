package ch.sbb.line.directory.service.hearing;

import ch.sbb.atlas.amazon.helper.FutureTimetableHelper;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.model.search.TimetableFieldNumberSearchRestrictions;
import ch.sbb.line.directory.service.TimetableFieldNumberService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimetableFieldNumberResolverService {

  private final TimetableFieldNumberService timetableFieldNumberService;

  public String resolveTtfnid(String timetableFieldNumber) {
    if (timetableFieldNumber != null) {
      LocalDate beginningOfNextTimetableYear = FutureTimetableHelper.getActualTimetableYearChangeDate(LocalDate.now());
      log.info("Resolving timetableFieldNumber={} at {} to ttfnid", timetableFieldNumber, beginningOfNextTimetableYear);

      Page<TimetableFieldNumber> timetableFieldNumbers = timetableFieldNumberService.getVersionsSearched(
          TimetableFieldNumberSearchRestrictions.builder()
              .pageable(Pageable.unpaged())
              .number(timetableFieldNumber)
              .validOn(Optional.of(beginningOfNextTimetableYear))
              .build());

      if (timetableFieldNumbers.getTotalElements() == 1) {
        String ttfnid = timetableFieldNumbers.getContent().get(0).getTtfnid();
        log.info("Resolved timetableFieldNumber={} at {} to ttfnid {}", timetableFieldNumber, beginningOfNextTimetableYear,
            ttfnid);
        return ttfnid;
      }
    }
    return null;
  }

  public List<TimetableHearingStatementModel> resolveAdditionalVersionInfo(List<TimetableHearingStatementModel> statements) {
    if (statements.isEmpty()) {
      return Collections.emptyList();
    }
    LocalDate validAtDateForYear = getFirstDayOfTimetableYear(statements);

    List<TimetableFieldNumberVersion> versions = timetableFieldNumberService.getVersionsValidAt(
        statements.stream().map(TimetableHearingStatementModel::getTtfnid).collect(
            Collectors.toSet()), validAtDateForYear);

    statements.stream()
        .filter(statement -> statement.getTtfnid() != null)
        .forEach(statement -> {
          TimetableFieldNumberVersion resolvedVersion = versions.stream()
              .filter(i -> i.getTtfnid().equals(statement.getTtfnid()))
              .findFirst().orElseThrow(() -> new IllegalArgumentException(statement.getTtfnid()));

          statement.setTimetableFieldNumber(resolvedVersion.getNumber());
          statement.setTimetableFieldDescription(resolvedVersion.getDescription());
        });

    return statements;
  }

  private static LocalDate getFirstDayOfTimetableYear(List<TimetableHearingStatementModel> statements) {
    if (statements.stream().map(TimetableHearingStatementModel::getTimetableYear).distinct().count() != 1) {
      throw new IllegalArgumentException("Statements should be from the same year for this");
    }
    return FutureTimetableHelper.getFirstDayOfTimetableYear(statements.get(0).getTimetableYear());
  }

}
