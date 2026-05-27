package ch.sbb.atlas.api.timetable.hearing.model;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true)
public abstract class BaseTimetableHearingStatementCsv {

  Long timetableHearingStatementId;
  String cantonAbbreviation;
  String timetableFieldNumber;
  String timetableFieldNumberDescription;
  String stopPlace;
  String transportCompanyAbbreviations;
  String transportCompanyDescriptions;
  Boolean documentsPresent;
  StatementStatus status;
  Long timetableHearingYear;
  String topic;

}
