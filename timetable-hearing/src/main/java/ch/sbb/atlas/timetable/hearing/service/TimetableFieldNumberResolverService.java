package ch.sbb.atlas.timetable.hearing.service;

import ch.sbb.atlas.amazon.helper.FutureTimetableHelper;
import ch.sbb.atlas.api.client.lidi.TimetableFieldNumberClient;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberModel;
import ch.sbb.atlas.api.model.Container;
import java.time.LocalDate;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimetableFieldNumberResolverService {

  private final TimetableFieldNumberClient timetableFieldNumberClient;

  public String resolveTtfnid(String timetableFieldNumber) {
    if (timetableFieldNumber != null) {
      LocalDate beginningOfNextTimetableYear = FutureTimetableHelper.getActualTimetableYearChangeDate(LocalDate.now());
      log.info("Resolving timetableFieldNumber={} at {} to ttfnid", timetableFieldNumber, beginningOfNextTimetableYear);

      Container<TimetableFieldNumberModel> timetableFieldNumbers = timetableFieldNumberClient.getOverview(Pageable.unpaged(),
          null, timetableFieldNumber, null, beginningOfNextTimetableYear, null);

      if (timetableFieldNumbers.getTotalCount() == 1) {
        String ttfnid = timetableFieldNumbers.getObjects().get(0).getTtfnid();
        log.info("Resolved timetableFieldNumber={} at {} to ttfnid {}", timetableFieldNumber, beginningOfNextTimetableYear,
            ttfnid);
        return ttfnid;
      }
    }
    return null;
  }

}
