package ch.sbb.atlas.api.bodi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.bodi.enumeration.BusinessType;
import ch.sbb.atlas.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldNameConstants
@Schema(name = "BusinessOrganisation")
public class BusinessOrganisationModel {

  @Schema(description = "Swiss Business Organisation ID (SBOID)", example = "ch:1:sboid:100052", accessMode =
      AccessMode.READ_ONLY)
  private String sboid;

  @Schema(description = "Swiss Administration ID (SAID)", example = "100052", accessMode = AccessMode.READ_ONLY)
  private String said;

  @Schema(description = "Description German", example = "Verkehrsbetriebe STI AG")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_60)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @NotNull
  private String descriptionDe;

  @Schema(description = "Description French", example = "Verkehrsbetriebe STI AG")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_60)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @NotNull
  private String descriptionFr;

  @Schema(description = "Description Italian", example = "Verkehrsbetriebe STI AG")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_60)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @NotNull
  private String descriptionIt;

  @Schema(description = "Description English", example = "Verkehrsbetriebe STI AG")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_60)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @NotNull
  private String descriptionEn;

  @Schema(description = "Abbreviation German", example = "STI")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_10)
  @Pattern(regexp = AtlasCharacterSetsRegex.ALPHA_NUMERIC)
  @NotNull
  private String abbreviationDe;

  @Schema(description = "Abbreviation French", example = "STI")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_10)
  @Pattern(regexp = AtlasCharacterSetsRegex.ALPHA_NUMERIC)
  @NotNull
  private String abbreviationFr;

  @Schema(description = "Abbreviation Italian", example = "STI")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_10)
  @Pattern(regexp = AtlasCharacterSetsRegex.ALPHA_NUMERIC)
  @NotNull
  private String abbreviationIt;

  @Schema(description = "Abbreviation English", example = "STI")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_10)
  @Pattern(regexp = AtlasCharacterSetsRegex.ALPHA_NUMERIC)
  @NotNull
  private String abbreviationEn;

  @Schema(description = "Organisation Number", example = "146")
  @Min(value = 0)
  @Max(value = 99999)
  private Integer organisationNumber;

  @Schema(description = "Enterprise E-Mail address", example = "hans.muster@enterprise.ch")
  @Pattern(regexp = AtlasCharacterSetsRegex.EMAIL_ADDRESS)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_255)
  private String contactEnterpriseEmail;

  @Schema(description = "Status", accessMode = AccessMode.READ_ONLY)
  private Status status;

  @Schema(description = "Business Types")
  private Set<BusinessType> businessTypes;

  @Schema(description = "Valid from")
  @NotNull
  private LocalDate validFrom;

  @Schema(description = "Valid to")
  @NotNull
  private LocalDate validTo;

}
