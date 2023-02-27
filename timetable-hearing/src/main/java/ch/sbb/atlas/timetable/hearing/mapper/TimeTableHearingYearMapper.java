package ch.sbb.atlas.timetable.hearing.mapper;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingYear;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeTableHearingYearMapper {

  public static TimetableHearingYear toEntity(TimetableHearingYearModel hearingYearModel) {
    return TimetableHearingYear.builder()
        .timetableYear(hearingYearModel.getTimetableYear())
        .hearingStatus(hearingYearModel.getHearingStatus())
        .hearingFrom(hearingYearModel.getHearingFrom())
        .hearingTo(hearingYearModel.getHearingTo())
        .statementCreatableExternal(hearingYearModel.isStatementCreatableExternal())
        .statementCreatableInternal(hearingYearModel.isStatementCreatableInternal())
        .statementEditable(hearingYearModel.isStatementEditable())
        .version(hearingYearModel.getEtagVersion())
        .build();
  }

  public static TimetableHearingYearModel toModel(TimetableHearingYear hearingYear) {
    return TimetableHearingYearModel.builder()
        .timetableYear(hearingYear.getTimetableYear())
        .hearingStatus(hearingYear.getHearingStatus())
        .hearingFrom(hearingYear.getHearingFrom())
        .hearingTo(hearingYear.getHearingTo())
        .statementCreatableExternal(hearingYear.isStatementCreatableExternal())
        .statementCreatableInternal(hearingYear.isStatementCreatableInternal())
        .statementEditable(hearingYear.isStatementEditable())
        .etagVersion(hearingYear.getVersion())
        .build();
  }
}
