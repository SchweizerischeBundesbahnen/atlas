package ch.sbb.line.directory.service.hearing;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.model.FutureTimetableHelper;
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
      log.info("Resolving timetableFieldNumber=[{}] at {} to ttfnid", timetableFieldNumber, beginningOfNextTimetableYear);

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

    List<TimetableFieldNumberVersion> versions = timetableFieldNumberService.getVersionsValidAt(
        statements.stream().map(TimetableHearingStatementModelV2::getTtfnid).collect(
            Collectors.toSet()), validAtDateForYear);

    statements.stream()
        .filter(statement -> statement.getTtfnid() != null)
        .forEach(statement -> {
          Optional<TimetableFieldNumberVersion> resolvedVersion = versions.stream()
              .filter(i -> i.getTtfnid().equals(statement.getTtfnid()))
              .findFirst();

          resolvedVersion.ifPresent(version -> {
            statement.setTimetableFieldNumber(version.getNumber());
            statement.setTimetableFieldDescription(version.getDescription());
          });
        });

    return statements;
  }

  private static LocalDate getFirstDayOfTimetableYear(List<TimetableHearingStatementModelV2> statements) {
    if (statements.stream().map(TimetableHearingStatementModelV2::getTimetableYear).distinct().count() != 1) {
      throw new IllegalArgumentException("Statements should be from the same year for this");
    }
    return FutureTimetableHelper.getFirstDayOfTimetableYear(statements.get(0).getTimetableYear());
  }

}
