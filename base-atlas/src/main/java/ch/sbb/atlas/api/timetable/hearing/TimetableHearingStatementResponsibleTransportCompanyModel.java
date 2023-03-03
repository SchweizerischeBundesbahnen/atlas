package ch.sbb.atlas.api.timetable.hearing;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotNull;
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
@Schema(name = "TimetableHearingStatementResponsibleTransportCompany")
public class TimetableHearingStatementResponsibleTransportCompanyModel {

  @Schema(description = "Technical identifier", example = "1", accessMode = AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "Technical identifier of referred TransportCompany", example = "1")
  @NotNull
  private Long transportCompanyId;

  @Schema(description = "TransportCompany number", example = "#0001")
  private String number;

  @Schema(description = "TransportCompany abbreviation", example = "SBB")
  private String abbreviation;

  @Schema(description = "TransportCompany business register name", example = "Schweizerische Bundesbahnen SBB")
  private String businessRegisterName;

}
