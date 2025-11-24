package ch.sbb.atlas.api.timetable.hearing.model;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Schema(name = "BatchUpdateTimetableHearingStatements")
public class BatchUpdateTimetableHearingStatementsModel extends BaseUpdateHearingModel {

  private StatementStatus statementStatus;

  private Long dossierId;

  private String dossierContactMail;
}
