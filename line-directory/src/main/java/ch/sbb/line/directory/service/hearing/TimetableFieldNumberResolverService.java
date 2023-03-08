package ch.sbb.line.directory.service.hearing;

import ch.sbb.atlas.amazon.helper.FutureTimetableHelper;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.model.search.TimetableFieldNumberSearchRestrictions;
import ch.sbb.line.directory.service.TimetableFieldNumberService;
import java.time.LocalDate;
import java.util.Optional;
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

}
