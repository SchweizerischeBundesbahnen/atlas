package ch.sbb.atlas.api.timetable.hearing;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@Schema(name = "TimetableHearingStatementV2")
public class TimetableHearingStatementModelV2 extends TimetableHearingStatementModel {

  @NotNull
  @Valid
  private TimetableHearingStatementSenderModelV2 statementSender;

}
