package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.lidi.TimetableYearChangeApiInternal;
import ch.sbb.atlas.model.FutureTimetableHelper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TimetableYearChangeControllerInternal implements TimetableYearChangeApiInternal {

  @Override
  public LocalDate getTimetableYearChange(int year) {
    return FutureTimetableHelper.getTimetableYearChangeDateToExportData(
        LocalDate.now().withYear(year));
  }

  @Override
  public List<LocalDate> getNextTimetablesYearChange(int count) {
    List<LocalDate> nextYearsFutureTimetables = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      LocalDate nextYear = LocalDate.now().plusYears(i);
      nextYearsFutureTimetables.add(
          FutureTimetableHelper.getTimetableYearChangeDateToExportData(nextYear));
    }
    return nextYearsFutureTimetables;
  }
}
