package ch.sbb.atlas.api.timetable.hearing;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.api.model.CantonAssociated;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
@Schema(name = "TimetableHearingStatement")
public class TimetableHearingStatementModel extends BaseVersionModel implements CantonAssociated {

  @Schema(description = "Technical identifier", example = "1", accessMode = AccessMode.READ_ONLY)
  private Long id;

  @Min(TimetableHearingConstants.MIN_YEAR)
  @Max(TimetableHearingConstants.MAX_YEAR)
  @Schema(description = "TimetableYear", example = "2024")
  private Long timetableYear;

  @Schema(description = "Current status")
  private StatementStatus statementStatus;

  @Schema(description = "TimetableFieldNumberId regarding the statement", example = "ch:1:ttfnid:123234")
  private String ttfnid;

  @Schema(description = "Timetable field number", example = "07.061", accessMode = AccessMode.WRITE_ONLY)
  private String timetableFieldNumber;

  @NotNull
  @Schema(description = "Canton, the statement is for")
  private SwissCanton swissCanton;

  @Size(max = AtlasFieldLengths.LENGTH_50)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Schema(description = "StopPlace information for the statement", example = "Bern, Wyleregg")
  private String stopPlace;

  private List<TimetableHearingStatementResponsibleTransportCompanyModel> responsibleTransportCompanies;

  @NotNull
  @Valid
  private TimetableHearingStatementSenderModel statementSender;

  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_5000)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Schema(description = "Statement of citizen", example = "I need some more busses please.")
  private String statement;

  @Size(max = TimetableHearingConstants.MAX_DOCUMENTS)
  @Schema(description = "List of uploaded documents")
  private List<TimetableHearingStatementDocumentModel> documents;

  @Size(max = AtlasFieldLengths.LENGTH_5000)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @Schema(description = "Statement of Federal office of transport", example = "We can absolutely do that.")
  private String justification;

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)", example = "5")
  private Integer etagVersion;

  public List<TimetableHearingStatementResponsibleTransportCompanyModel> getResponsibleTransportCompanies() {
    return Objects.requireNonNullElseGet(responsibleTransportCompanies, ArrayList::new);
  }

  public List<TimetableHearingStatementDocumentModel> getDocuments() {
    return Objects.requireNonNullElseGet(documents, ArrayList::new);
  }
}
