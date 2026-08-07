package ch.sbb.timetable.hearing.service;

import ch.sbb.atlas.api.lidi.TimetableFieldNumberApiInternal;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberModel;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.model.FutureTimetableHelper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimetableFieldNumberResolverService {

  private final TimetableFieldNumberApiInternal timetableFieldNumberApiInternal;

  public String resolveTtfnid(String timetableFieldNumber) {
    if (timetableFieldNumber != null) {
      LocalDate beginningOfNextTimetableYear = FutureTimetableHelper.getActualTimetableYearChangeDate(LocalDate.now());
      log.info("Resolving timetableFieldNumber=[{}] at {} to ttfnid", timetableFieldNumber, beginningOfNextTimetableYear);

      List<TimetableFieldNumberModel> timetableFieldNumbers = timetableFieldNumberApiInternal.getOverview(Pageable.unpaged(),
              Collections.emptyList(), timetableFieldNumber, null, beginningOfNextTimetableYear, Collections.emptyList())
          .getObjects();

      if (timetableFieldNumbers.size() == 1) {
        String ttfnid = timetableFieldNumbers.getFirst().getTtfnid();
        log.info("Resolved timetableFieldNumber={} at {} to ttfnid {}", timetableFieldNumber, beginningOfNextTimetableYear,
            ttfnid);
        return ttfnid;
      } else {
        log.info("Could not resolve timetableFieldNumber={}, page was timetableFieldNumbers={}", timetableFieldNumber,
            timetableFieldNumbers);
      }
    }
    log.info("No timetableFieldNumber given.");
    return null;
  }

  public List<TimetableHearingStatementModelV2> resolveAdditionalVersionInfo(List<TimetableHearingStatementModelV2> statements) {
    if (statements.isEmpty()) {
      return Collections.emptyList();
    }
    LocalDate validAtDateForYear = getFirstDayOfTimetableYear(statements);

    Set<String> ttfnIds = statements.stream().map(TimetableHearingStatementModelV2::getTtfnid).collect(Collectors.toSet());
    List<TimetableFieldNumberVersionModel> versions = timetableFieldNumberApiInternal.getVersionsValidAt(ttfnIds,
        validAtDateForYear);

    statements.stream()
        .filter(statement -> statement.getTtfnid() != null)
        .forEach(statement -> {
          Optional<TimetableFieldNumberVersionModel> resolvedVersion = versions.stream()
              .filter(i -> i.getTtfnid().equals(statement.getTtfnid()))
              .findFirst();

          resolvedVersion.ifPresent(version -> {
            statement.setTimetableFieldNumber(version.getNumber());
            statement.setTimetableFieldDescription(version.getDescriptionOutwardLine1());
          });
        });

    return statements;
  }

  private static LocalDate getFirstDayOfTimetableYear(List<TimetableHearingStatementModelV2> statements) {
    if (statements.stream().map(TimetableHearingStatementModelV2::getTimetableYear).distinct().count() != 1) {
      throw new IllegalArgumentException("Statements should be from the same year for this");
    }
    return FutureTimetableHelper.getFirstDayOfTimetableYear(statements.getFirst().getTimetableYear());
  }

}
