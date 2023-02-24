package ch.sbb.atlas.timetable.hearing.model;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
public class TimetableFieldNumberInformation {

  private String ttfnid;

  private String swissTimetableFieldNumber;

  private String timetableFieldDescription;

  public static TimetableFieldNumberInformation fromStatementModel(TimetableHearingStatementModel statement) {
    return TimetableFieldNumberInformation.builder()
        .ttfnid(statement.getTtfnid())
        .swissTimetableFieldNumber(statement.getSwissTimetableFieldNumber())
        .timetableFieldDescription(statement.getTimetableFieldDescription())
        .build();
  }

}
