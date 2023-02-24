package ch.sbb.atlas.timetable.hearing.controller;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementApiV1;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingStatement;
import ch.sbb.atlas.timetable.hearing.mapper.TimeTableHearingStatementMapper;
import ch.sbb.atlas.timetable.hearing.model.TimetableFieldNumberInformation;
import ch.sbb.atlas.timetable.hearing.service.TimetableFieldNumberResolverService;
import ch.sbb.atlas.timetable.hearing.service.TimetableHearingStatementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TimetableHearingStatementController implements TimetableHearingStatementApiV1 {

  private final TimetableHearingStatementService timetableHearingStatementService;
  private final TimetableFieldNumberResolverService timetableFieldNumberResolverService;

  // TODO: getter für übersichten nach Jahr und Kanton

  @Override
  public TimetableHearingStatementModel createStatement(TimetableHearingStatementModel statement) {
    String resolvedTtfnid =
        timetableFieldNumberResolverService.resolveTtfnid(TimetableFieldNumberInformation.fromStatementModel(statement));
    statement.setTtfnid(resolvedTtfnid);

    TimetableHearingStatement hearingStatement = timetableHearingStatementService.createHearingStatement(
        TimeTableHearingStatementMapper.toEntity(statement));
    return TimeTableHearingStatementMapper.toModel(hearingStatement);
  }

  @Override
  public TimetableHearingStatementModel updateStatement(TimetableHearingStatementModel statement) {
    String resolvedTtfnid =
        timetableFieldNumberResolverService.resolveTtfnid(TimetableFieldNumberInformation.fromStatementModel(statement));
    statement.setTtfnid(resolvedTtfnid);

    TimetableHearingStatement hearingStatement = timetableHearingStatementService.updateHearingStatement(
        TimeTableHearingStatementMapper.toEntity(statement));
    return TimeTableHearingStatementMapper.toModel(hearingStatement);
  }

}
