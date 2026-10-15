package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.api.lidi.enumaration.v3.ConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.v3.Financing;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineMeanOfTransport;
import ch.sbb.atlas.validation.DatesValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
public abstract class BaseLineVersionModelV3 implements DatesValidator {

  @Schema(description = "Description", example = "Val-Surses-Bus")
  @NotBlank
  @Size(min = 2, max = AtlasFieldLengths.LENGTH_255)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String description;

  @Schema(description = "LongName", example = "Savognin - Pensa / Plang la Curvanera")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_255)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String longName;

  @Schema(description = "Number - ignored for sublines, resolved from the referenced line", example = "L1")
  @Size(max = AtlasFieldLengths.LENGTH_8)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String number;

  @Schema(description = "ShortNumber - ignored for sublines, resolved from the referenced line", example = "61")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_10)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String shortNumber;

  @Schema(description = "OfferCategory - ignored for sublines, resolved from the referenced line")
  private OfferCategory offerCategory;

  @Schema(description = "SwissLineNumber", example = "b1.L1")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @Pattern(regexp = AtlasCharacterSetsRegex.SID4PT)
  private String swissLineNumber;

  @Schema(description = "ConcessionType - required for ORDERLY lines and CONCESSION sublines; ignored for other sublines, "
      + "resolved from the referenced line")
  private ConcessionType concessionType;

  @Schema(description = "LinienID / LineRef - Only available temporarily until SLNID Migration is complete", example = "583d")
  @Size(max = AtlasFieldLengths.LENGTH_20)
  @Pattern(regexp = AtlasCharacterSetsRegex.ALPHA_NUMERIC_WITH_COLON)
  private String linienId;

  @Schema(description = "BusinessOrganisation SBOID", example = "ch:1:sboid:100001")
  @NotBlank
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String businessOrganisation;

  @Schema(description = "MeansOfTransport of this version - never inherited from the referenced line")
  private Set<LineMeanOfTransport> meansOfTransport;

  @Schema(description = "Financing of this version - never inherited from the referenced line")
  private Financing financing;

  @Schema(description = "Anlagennummer - only for cableway means of transport", example = "3521")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String cablewayNumber;

  @Schema(description = "Anlage-Typ - only for cableway means of transport", example = "Pendelbahn")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String cablewayType;

  @Schema(description = "Anlage-Name - only for cableway means of transport", example = "Luftseilbahn Wengen - Maennlichen")
  @Size(max = AtlasFieldLengths.LENGTH_255)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String cablewayName;

  @Schema(description = "Comment", example = "Die Linie wird von einem Bus bedient.")
  @Size(max = AtlasFieldLengths.LENGTH_1500)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String comment;

  @Schema(description = "Valid from")
  @NotNull
  private LocalDate validFrom;

  @Schema(description = "Valid to")
  @NotNull
  private LocalDate validTo;

}
