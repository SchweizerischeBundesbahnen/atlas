package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.api.lidi.enumaration.v3.ConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.v3.Financing;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineMeanOfTransport;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineObjectType;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.api.model.BaseVersionModel;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "LineVersionV3")
public class LineVersionModelV3 extends BaseVersionModel {

  @Schema(description = """
      This ID helps identify versions of a line in the use case front end and/or update.
      This ID can be deleted if the version is no longer present. Do not use this ID to map your object to a line.
      To do this, use the slnid in combination with the data range (valid from/valid until).
      """,
      accessMode = AccessMode.READ_ONLY, example = "1")
  private Long id;

  @Schema(description = "SLNID", accessMode = AccessMode.READ_ONLY, example = "ch:1:slnid:1028629")
  private String slnid;

  @Schema(description = "Discriminator derived from the presence of a referenced line", accessMode = AccessMode.READ_ONLY)
  private LineObjectType type;

  @Schema(description = "Number", accessMode = AccessMode.READ_ONLY, example = "90.583")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String number;

  @Schema(description = "Description", accessMode = AccessMode.READ_ONLY, example = "Val-Surses-Bus")
  @Size(min = 2, max = AtlasFieldLengths.LENGTH_255)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String description;

  @Schema(description = "LongName", accessMode = AccessMode.READ_ONLY, example = "Savognin - Pensa / Plang la Curvanera")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_255)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String longName;

  @Schema(description = "SwissLineNumber", accessMode = AccessMode.READ_ONLY, example = "90.583")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  private String swissLineNumber;

  @Schema(description = "LinienID / LineRef - Only available temporarily until SLNID Migration is complete", example = "583d")
  @Size(max = AtlasFieldLengths.LENGTH_20)
  @Pattern(regexp = AtlasCharacterSetsRegex.ALPHA_NUMERIC_WITH_COLON)
  private String linienId;

  @Schema(description = "LineType", accessMode = AccessMode.READ_ONLY)
  private LineType lineType;

  @Schema(description = "BusinessOrganisation SBOID", accessMode = AccessMode.READ_ONLY, example = "ch:1:sboid:1100048")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String businessOrganisation;

  @Schema(description = "MeansOfTransport of this version - never inherited from the referenced line",
      accessMode = AccessMode.READ_ONLY)
  private Set<LineMeanOfTransport> meansOfTransport;

  @Schema(description = "ConcessionType", accessMode = AccessMode.READ_ONLY)
  private ConcessionType concessionType;

  @Schema(description = "OfferCategory", accessMode = AccessMode.READ_ONLY)
  private OfferCategory offerCategory;

  @Schema(description = "Financing of this version - never inherited from the referenced line",
      accessMode = AccessMode.READ_ONLY)
  private Financing financing;

  @Schema(description = "Anlagennummer - only populated for cableway means of transport", accessMode = AccessMode.READ_ONLY,
      example = "3521")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String cablewayNumber;

  @Schema(description = "Anlage-Typ - only populated for cableway means of transport", accessMode = AccessMode.READ_ONLY,
      example = "Pendelbahn")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String cablewayType;

  @Schema(description = "Anlage-Name - only populated for cableway means of transport", accessMode = AccessMode.READ_ONLY,
      example = "Luftseilbahn Wengen - Maennlichen")
  @Size(max = AtlasFieldLengths.LENGTH_255)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String cablewayName;

  @Schema(description = "Comment", accessMode = AccessMode.READ_ONLY, example = "Die Linie wird von einem Bus bedient.")
  @Size(max = AtlasFieldLengths.LENGTH_1500)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String comment;

  @Schema(description = "Direct parent line - null for LINE and for unresolvable references", accessMode = AccessMode.READ_ONLY)
  private ReferencedLineModelV3 referencedLine;

  @Schema(description = "Valid from", accessMode = AccessMode.READ_ONLY)
  private LocalDate validFrom;

  @Schema(description = "Valid to", accessMode = AccessMode.READ_ONLY)
  private LocalDate validTo;

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)", example = "5")
  private Integer etagVersion;

}
