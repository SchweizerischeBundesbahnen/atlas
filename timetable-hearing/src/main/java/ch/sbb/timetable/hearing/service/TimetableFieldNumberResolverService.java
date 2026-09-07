package ch.sbb.timetable.hearing.service;

import ch.sbb.atlas.api.lidi.TimetableFieldNumberApiInternal;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
      log.info("Resolving timetableFieldNumber=[{}] to ttfnid", timetableFieldNumber);

      List<TimetableFieldNumberModel> timetableFieldNumbers = timetableFieldNumberApiInternal.getOverview(Pageable.unpaged(),
              Collections.emptyList(), timetableFieldNumber, null, null, Collections.emptyList(), Collections.emptyList())
          .getObjects();

      if (timetableFieldNumbers.size() == 1) {
        String ttfnid = timetableFieldNumbers.getFirst().getTtfnid();
        log.info("Resolved timetableFieldNumber={} to ttfnid {}", timetableFieldNumber, ttfnid);
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

    List<String> ttfnIds = statements.stream()
        .map(TimetableHearingStatementModelV2::getTtfnid)
        .filter(Objects::nonNull)
        .distinct()
        .toList();
    if (ttfnIds.isEmpty()) {
      return statements;
    }

    List<TimetableFieldNumberModel> timetableFieldNumbers = timetableFieldNumberApiInternal.getOverview(Pageable.unpaged(),
        Collections.emptyList(), null, null, null, Collections.emptyList(), ttfnIds).getObjects();

    statements.stream()
        .filter(statement -> statement.getTtfnid() != null)
        .forEach(statement -> timetableFieldNumbers.stream()
            .filter(timetableFieldNumber -> timetableFieldNumber.getTtfnid().equals(statement.getTtfnid()))
            .findFirst()
            .ifPresent(timetableFieldNumber -> {
              statement.setTimetableFieldNumber(timetableFieldNumber.getNumber());
              statement.setTimetableFieldDescription(timetableFieldNumber.getDescriptionOutwardLine1());
            }));

    return statements;
  }

}
