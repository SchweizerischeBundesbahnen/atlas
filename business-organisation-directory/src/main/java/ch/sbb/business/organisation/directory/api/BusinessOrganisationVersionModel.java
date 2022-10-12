package ch.sbb.business.organisation.directory.api;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.api.BaseModel;
import ch.sbb.atlas.base.service.model.validation.DatesValidator;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
@Schema(name = "BusinessOrganisationVersion")
public class BusinessOrganisationVersionModel extends BaseModel implements DatesValidator {

  @Schema(description = "Technical identifier", accessMode = AccessMode.READ_ONLY, example = "1")
  private Long id;

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
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @NotNull
  private String abbreviationDe;

  @Schema(description = "Abbreviation French", example = "STI")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_10)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @NotNull
  private String abbreviationFr;

  @Schema(description = "Abbreviation Italian", example = "STI")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_10)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @NotNull
  private String abbreviationIt;

  @Schema(description = "Abbreviation English", example = "STI")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_10)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  @NotNull
  private String abbreviationEn;

  @Schema(description = "Organisation Number", example = "146")
  @Min(value = 0)
  @Max(value = 99999)
  @NotNull
  private Integer organisationNumber;

  @Schema(description = "Enterprise E-Mail address", example = "hans.muster@enterprise.ch")
  @Pattern(regexp = AtlasCharacterSetsRegex.EMAIL_ADDRESS)
  @Size(max = AtlasFieldLengths.LENGTH_255)
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

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)", example = "5",
      accessMode = AccessMode.READ_ONLY)
  private Integer etagVersion;

  public static BusinessOrganisationVersion toEntity(BusinessOrganisationVersionModel model) {
    return BusinessOrganisationVersion.builder()
        .id(model.getId())
        .status(model.getStatus())
        .descriptionDe(model.getDescriptionDe())
        .descriptionFr(model.getDescriptionFr())
        .descriptionIt(model.getDescriptionIt())
        .descriptionEn(model.getDescriptionEn())
        .abbreviationDe(model.getAbbreviationDe())
        .abbreviationFr(model.getAbbreviationFr())
        .abbreviationIt(model.getAbbreviationIt())
        .abbreviationEn(model.getAbbreviationEn())
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .organisationNumber(model.getOrganisationNumber())
        .contactEnterpriseEmail(model.getContactEnterpriseEmail())
        .sboid(model.getSboid())
        .businessTypes(model.getBusinessTypes())
        .version(model.getEtagVersion())
        .build();
  }

  public static BusinessOrganisationVersionModel toModel(BusinessOrganisationVersion entity) {
    return BusinessOrganisationVersionModel
        .builder()
        .id(entity.getId())
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
        .etagVersion(entity.getVersion())
        .said(SboidToSaidConverter.toSaid(entity.getSboid()))
        .businessTypes(entity.getBusinessTypes())
        .creator(entity.getCreator())
        .creationDate(entity.getCreationDate())
        .editor(entity.getEditor())
        .editionDate(entity.getEditionDate())
        .build();
  }

}
