package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.enumaration.TtfnMeanOfTransport;
import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.model.IdCheckable;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.validation.DatesValidator;
import ch.sbb.atlas.validation.TrimmedNotBlank;
import ch.sbb.atlas.validation.ValidTtfnDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ValidTtfnDescription
@Schema(name = "TimetableFieldNumberVersion")
public class TimetableFieldNumberVersionModel extends BaseVersionModel implements DatesValidator, IdCheckable {

  @Schema(description = """
      This ID helps identify versions of a timetable field number in the use case front end and/or update.
      This ID can be deleted if the version is no longer present. Do not use this ID to map your object to a timetable field number.
      To do this, use the ttfnid  in combination with the data range (valid from/valid until).
      """,
      example = "1")
  private Long id;

  @Schema(description = "Timetable field number identifier", example = "ch:1:ttfnid:100000", accessMode = AccessMode.READ_ONLY)
  private String ttfnid;

  @Schema(description = "Description outward line one", example = "Como - Chiasso - Mendrisio - Varese (S40)")
  @NotNull
  @TrimmedNotBlank
  @Size(max = AtlasFieldLengths.LENGTH_70)
  private String descriptionOutwardLine1;

  @Schema(description = "Description outward line two", example = "Bellinzona - Mendrisio - Varese - Malpensa Aeroporto (S50)")
  @TrimmedNotBlank
  @Size(max = AtlasFieldLengths.LENGTH_70)
  private String descriptionOutwardLine2;

  @Schema(description = "Description outward line three", example = "(Ferrovia - Regionale TILO-Linea S40, S50)")
  @TrimmedNotBlank
  @Size(max = AtlasFieldLengths.LENGTH_70)
  private String descriptionOutwardLine3;

  @Schema(description = "Description return line one", example = "Varese - Mendrisio - Chiasso - Como (S40)")
  @TrimmedNotBlank
  @Size(max = AtlasFieldLengths.LENGTH_70)
  private String descriptionReturnLine1;

  @Schema(description = "Description return line two", example = "Malpensa Aeroporto - Varese - Mendrisio - Bellinzona (S50)")
  @TrimmedNotBlank
  @Size(max = AtlasFieldLengths.LENGTH_70)
  private String descriptionReturnLine2;

  @Schema(description = "Description return line three", example = "(Ferrovia - Regionale TILO-Linea S40, S50)")
  @TrimmedNotBlank
  @Size(max = AtlasFieldLengths.LENGTH_70)
  private String descriptionReturnLine3;

  @Schema(description = "Mean of transport")
  @NotNull
  private TtfnMeanOfTransport meanOfTransport;

  @Schema(description = "Number", example = "100; 80.099; 2700")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @NotNull
  @Pattern(regexp = AtlasCharacterSetsRegex.TTFN_NUMBER)
  private String number;

  @Schema(description = "Timetable field number", example = "b0.123")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
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

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)", example = "5")
  private Integer etagVersion;
}
