package ch.sbb.line.directory.api;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.api.BaseModel;
import ch.sbb.atlas.base.service.model.validation.DatesValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@Schema(name = "TimetableFieldNumberVersion")
public class TimetableFieldNumberVersionModel extends BaseModel implements DatesValidator {

  @Schema(description = "Technical identifier", example = "1")
  private Long id;

  @Schema(description = "Timetable field number identifier", example = "ch:1:ttfnid:100000",
      accessMode = AccessMode.READ_ONLY)
  private String ttfnid;

  @Schema(description = "Description", example = "Fribourg/Freiburg - Bern - Thun (S-Bahn Bern, "
      + "Linien S1, S2)")
  @Size(max = AtlasFieldLengths.LENGTH_255)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String description;

  @Schema(description = "Number", example = "100; 80.099; 2700")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @NotNull
  @Pattern(regexp = AtlasCharacterSetsRegex.NUMERIC_WITH_DOT)
  private String number;

  @Schema(description = "Timetable field number", example = "b0.123")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @NotNull
  @Pattern(regexp = AtlasCharacterSetsRegex.SID4PT)
  private String swissTimetableFieldNumber;

  @Schema(description = "Status", accessMode = AccessMode.READ_ONLY)
  private Status status;

  @Schema(description = "Date - valid from", example = "2021-11-23")
  @NotNull
  private LocalDate validFrom;

  @Schema(description = "Date - valid to", example = "2021-12-01")
  @NotNull
  private LocalDate validTo;

  @Schema(description = "BusinessOrganisation SBOID", example = "ch:1:sboid:100001")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @NotNull
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String businessOrganisation;

  @Schema(description = "Additional comment", example = "Hier kann für interne Zwecke ein "
      + "Kommentar welcher das Fahrplanfeld betrifft erfasst werden.")
  @Size(max = AtlasFieldLengths.LENGTH_1500)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String comment;

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see "
      + "RFC7232:Section 2.3)", example = "5")
  private Integer etagVersion;
}
