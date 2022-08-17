package ch.sbb.line.directory.controller;

import ch.sbb.atlas.amazon.helper.FutureTimetableHelper;
import ch.sbb.line.directory.api.FutureTimetableApiV1;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class FutureTimetableController implements FutureTimetableApiV1 {

  @Override
  public LocalDate getFutureTimetable(int year) {
    return FutureTimetableHelper.getFutureTimetableExportDate(LocalDate.now().withYear(year));
  }

  @Override
  public List<LocalDate> getNextYearsFutureTimetables(int count) {
    List<LocalDate> nextYearsFutureTimetables = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      LocalDate nextYear = LocalDate.now().plusYears(i);
      nextYearsFutureTimetables.add(FutureTimetableHelper.getFutureTimetableExportDate(nextYear));
    }
    return nextYearsFutureTimetables;
  }
}
