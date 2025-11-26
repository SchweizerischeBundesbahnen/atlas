package ch.sbb.atlas.api.timetable.hearing;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.CantonAssociated;
import ch.sbb.atlas.kafka.model.SwissCanton;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@EqualsAndHashCode
@FieldNameConstants
@Schema(name = "ExternalTimetableHearingStatementCreate")
public class ExternalTimetableHearingStatementCreateModel implements CantonAssociated, TimetableHearingStatement {

  @Schema(description = "TimetableFieldNumberId regarding the statement", example = "ch:1:ttfnid:123234")
  private String ttfnid;

  @Schema(description = "Timetable field number", example = "100; 80.099; 2700")
  private String timetableFieldNumber;

  @NotNull
  @Schema(description = "Canton, the statement is for")
  private SwissCanton swissCanton;

  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_5000)
  @Schema(description = "Statement of citizen", example = "I need some more busses please.")
  private String statement;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  @Schema(description = "StopPlace information for the statement", example = "Bern, Wyleregg")
  private String stopPlace;

  @NotNull
  @Valid
  private TimetableHearingStatementSenderModelV2 statementSender;

}
