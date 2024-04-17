package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementApiV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.line.directory.entity.TimetableHearingYear;
import ch.sbb.line.directory.entity.TimetableHearingYear_;
import ch.sbb.line.directory.exception.ForbiddenDueToHearingYearSettingsException;
import ch.sbb.line.directory.service.hearing.TimetableHearingStatementService;
import ch.sbb.line.directory.service.hearing.TimetableHearingYearService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TimetableHearingStatementControllerV2 implements TimetableHearingStatementApiV2 {

  private final TimetableHearingStatementService timetableHearingStatementService;
  private final TimetableHearingYearService timetableHearingYearService;

  @Override
  public TimetableHearingStatementModel createStatementV2(TimetableHearingStatementModel statement,
      List<MultipartFile> documents) {
    TimetableHearingYear hearingYear = timetableHearingYearService.getHearingYear(statement.getTimetableYear());
    if (!hearingYear.isStatementCreatableInternal()) {
      throw new ForbiddenDueToHearingYearSettingsException(hearingYear.getTimetableYear(),
          TimetableHearingYear_.STATEMENT_CREATABLE_INTERNAL);
    }
    return timetableHearingStatementService.createHearingStatement(statement, documents);
  }

}
