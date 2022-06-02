package ch.sbb.business.organisation.directory.api;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.model.api.AtlasFieldLengths;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisation;
import ch.sbb.business.organisation.directory.entity.BusinessType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDate;
import java.util.Set;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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

  @Schema(description = "Swiss Business Organisation ID (SBOID)", example = "ch:1:sboid:100052", accessMode = AccessMode.READ_ONLY)
  private String sboid;

  @Schema(description = "Swiss Administration ID (SAID)", example = "100052", accessMode = AccessMode.READ_ONLY)
  private String said;

  @Schema(description = "Description German")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_60)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @NotNull
  private String descriptionDe;

  @Schema(description = "Description French")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_60)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @NotNull
  private String descriptionFr;

  @Schema(description = "Description Italian")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_60)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @NotNull
  private String descriptionIt;

  @Schema(description = "Description English")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_60)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @NotNull
  private String descriptionEn;

  @Schema(description = "Abbreviation German")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_10)
  @Pattern(regexp = AtlasCharacterSetsRegex.ALPHA_NUMERIC)
  @NotNull
  private String abbreviationDe;

  @Schema(description = "Abbreviation French")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_10)
  @Pattern(regexp = AtlasCharacterSetsRegex.ALPHA_NUMERIC)
  @NotNull
  private String abbreviationFr;

  @Schema(description = "Abbreviation Italian")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_10)
  @Pattern(regexp = AtlasCharacterSetsRegex.ALPHA_NUMERIC)
  @NotNull
  private String abbreviationIt;

  @Schema(description = "Abbreviation English")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_10)
  @Pattern(regexp = AtlasCharacterSetsRegex.ALPHA_NUMERIC)
  @NotNull
  private String abbreviationEn;

  @Schema(description = "Organisation Number")
  @Min(value = 0)
  @Max(value = 99999)
  private Integer organisationNumber;

  @Schema(description = "Enterprise E-Mail address")
  @Pattern(regexp = AtlasCharacterSetsRegex.EMAIL_ADDRESS)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_255)
  private String contactEnterpriseEmail;

  @Schema(description = "Status", accessMode = AccessMode.READ_ONLY)
  private Status status;

  @Schema(description = "Business Types")
  private Set<BusinessType> businessTypes;

  @Schema(description = "Business Types ID pipe separated", accessMode = AccessMode.READ_ONLY)
  private String types;

  @Schema(description = "Valid from")
  @NotNull
  private LocalDate validFrom;

  @Schema(description = "Valid to")
  @NotNull
  private LocalDate validTo;

  public static BusinessOrganisationModel toModel(BusinessOrganisation entity) {
    return BusinessOrganisationModel
        .builder()
        .status(entity.getStatus())
        .descriptionDe(entity.getDescriptionDe())
        .descriptionFr(entity.getDescriptionFr())
        .descriptionIt(entity.getDescriptionIt())
        .descriptionEn(entity.getDescriptionEn())
        .abbreviationDe(entity.getAbbreviationDe())
        .abbreviationFr(entity.getAbbreviationFr())
        .abbreviationIt(entity.getAbbreviationIt())
        .abbreviationEn(entity.getAbbreviationEn())
        .validFrom(entity.getValidFrom())
        .validTo(entity.getValidTo())
        .organisationNumber(entity.getOrganisationNumber())
        .contactEnterpriseEmail(entity.getContactEnterpriseEmail())
        .sboid(entity.getSboid())
        .said(SboidToSaidConverter.toSaid(entity.getSboid()))
        .businessTypes(entity.getBusinessTypes())
        .types(BusinessType.getBusinessTypesPiped(entity.getBusinessTypes()))
        .build();
  }

}
