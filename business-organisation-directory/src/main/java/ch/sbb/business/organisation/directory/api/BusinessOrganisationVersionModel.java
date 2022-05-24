package ch.sbb.business.organisation.directory.api;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.validation.DatesValidator;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.entity.BusinessType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDate;
import java.util.Set;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
@Schema(name = "BusinessOrganisationVersion")
public class BusinessOrganisationVersionModel implements DatesValidator {

  @Schema(description = "Technical identifier", accessMode = AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "Swiss Business Organisation ID (SBOID)", example = "ch:1:sboid:100052")
  private String sboid;

  @Schema(description = "Swiss Administration ID (SAID)", example = "100052")
  private String said;

  @Schema(description = "Description German")
  @Size(min = 1, max = 60)
  private String descriptionDe;

  @Schema(description = "Description French")
  @Size(min = 1, max = 60)
  private String descriptionFr;

  @Schema(description = "Description Italian")
  @Size(min = 1, max = 60)
  private String descriptionIt;

  @Schema(description = "Description English")
  @Size(min = 1, max = 60)
  private String descriptionEn;

  @Schema(description = "Abbreviation German")
  @Size(min = 1, max = 10)
  private String abbreviationDe;

  @Schema(description = "Abbreviation French")
  @Size(min = 1, max = 10)
  private String abbreviationFr;

  @Schema(description = "Abbreviation Italian")
  @Size(min = 1, max = 10)
  private String abbreviationIt;

  @Schema(description = "Abbreviation English")
  @Size(min = 1, max = 10)
  private String abbreviationEn;

  @Schema(description = "Organisation Number")
  @Min(value = 0)
  @Max(value = 99999)
  private Integer organisationNumber;

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

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)", example = "5")
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
        .said(getSaid(entity.getSboid()))
        .businessTypes(entity.getBusinessTypes())
        .types(BusinessType.getBusinessTypesPiped(entity.getBusinessTypes()))
        .build();
  }

  private static int INDEX_SBOID = 11;

  private static String getSaid(String sboid) {
    return sboid.substring(INDEX_SBOID);
  }

}
