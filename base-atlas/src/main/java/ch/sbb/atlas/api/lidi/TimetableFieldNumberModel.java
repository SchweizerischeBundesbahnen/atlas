package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "TimetableFieldNumber")
public class TimetableFieldNumberModel {

  @Schema(description = "Timetable field number", example = "b0.123")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @NotNull
  private String swissTimetableFieldNumber;

  @Schema(description = "Number", example = "100; 80.099; 2700")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @NotNull
  @Pattern(regexp = AtlasCharacterSetsRegex.NUMERIC_WITH_DOT)
  private String number;

  @Schema(description = "Timetable field number identifier", example = "ch:1:fpfnid:100000")
  private String ttfnid;

  @Schema(description = "Description", example = "Fribourg/Freiburg - Bern - Thun (S-Bahn Bern, Linien S1, S2)")
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String description;

  @Schema(description = "Status", accessMode = AccessMode.READ_ONLY)
  @Enumerated(EnumType.STRING)
  @NotNull
  private Status status;

  @Schema(description = "BusinessOrganisation SBOID", example = "ch:1:sboid:100001")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @NotNull
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String businessOrganisation;

  @Schema(description = "Date - valid from", example = "2021-11-23")
  @NotNull
  private LocalDate validFrom;

  @Schema(description = "Date - valid to", example = "2021-12-01")
  @NotNull
  private LocalDate validTo;

}
