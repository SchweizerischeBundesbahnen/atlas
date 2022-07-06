package ch.sbb.business.organisation.directory.api;

import ch.sbb.business.organisation.directory.entity.BusinessOrganisation;
import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldNameConstants
@Builder
@Schema(name = "TransportCompanyBoRelation")
public class TransportCompanyBoRelationModel {

  @Schema(description = "Transport Company Relation Id")
  private Long id;

  @Schema(description = "Swiss Administration ID (SAID)")
  private String said;

  @Schema(description = "Organisation Number")
  private Integer organisationNumber;

  @Schema(description = "Abbreviation")
  private String abbreviation;

  @Schema(description = "Description")
  private String description;

  @Schema(description = "Valid From")
  private LocalDate validFrom;

  @Schema(description = "Valid To")
  private LocalDate validTo;

  public static TransportCompanyBoRelationModel toModel(
      BusinessOrganisation businessOrganisation,
      TransportCompanyRelation transportCompanyRelation,
      String language) {

    String description;
    String abbreviation;

    switch (language) {
      case "fr":
        description = businessOrganisation.getDescriptionFr();
        abbreviation = businessOrganisation.getAbbreviationFr();
        break;
      case "it":
        description = businessOrganisation.getDescriptionIt();
        abbreviation = businessOrganisation.getAbbreviationIt();
        break;
      case "en":
        description = businessOrganisation.getDescriptionEn();
        abbreviation = businessOrganisation.getAbbreviationEn();
        break;
      default:
        description = businessOrganisation.getDescriptionDe();
        abbreviation = businessOrganisation.getAbbreviationDe();
        break;
    }

    return TransportCompanyBoRelationModel.builder()
                                          .id(transportCompanyRelation.getId())
                                          .said(SboidToSaidConverter.toSaid(
                                              transportCompanyRelation.getSboid()))
                                          .organisationNumber(
                                              businessOrganisation.getOrganisationNumber())
                                          .abbreviation(abbreviation)
                                          .description(description)
                                          .validFrom(transportCompanyRelation.getValidFrom())
                                          .validTo(transportCompanyRelation.getValidTo()).build();
  }
}
