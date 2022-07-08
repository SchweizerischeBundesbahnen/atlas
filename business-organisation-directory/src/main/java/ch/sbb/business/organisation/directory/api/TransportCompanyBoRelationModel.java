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

  @Schema(description = "Description German")
  private String descriptionDe;

  @Schema(description = "Description French")
  private String descriptionFr;

  @Schema(description = "Description Italian")
  private String descriptionIt;

  @Schema(description = "Description English")
  private String descriptionEn;

  @Schema(description = "Abbreviation German")
  private String abbreviationDe;

  @Schema(description = "Abbreviation French")
  private String abbreviationFr;

  @Schema(description = "Abbreviation Italian")
  private String abbreviationIt;

  @Schema(description = "Abbreviation English")
  private String abbreviationEn;

  @Schema(description = "Valid From")
  private LocalDate validFrom;

  @Schema(description = "Valid To")
  private LocalDate validTo;

  public static TransportCompanyBoRelationModel toModel(
      BusinessOrganisation businessOrganisation,
      TransportCompanyRelation transportCompanyRelation) {
    return TransportCompanyBoRelationModel.builder()
                                          .id(transportCompanyRelation.getId())
                                          .said(SboidToSaidConverter.toSaid(
                                              transportCompanyRelation.getSboid()))
                                          .organisationNumber(
                                              businessOrganisation.getOrganisationNumber())
                                          .descriptionDe(businessOrganisation.getDescriptionDe())
                                          .descriptionFr(businessOrganisation.getDescriptionFr())
                                          .descriptionIt(businessOrganisation.getDescriptionIt())
                                          .descriptionEn(businessOrganisation.getDescriptionEn())
                                          .abbreviationDe(businessOrganisation.getAbbreviationDe())
                                          .abbreviationFr(businessOrganisation.getAbbreviationFr())
                                          .abbreviationIt(businessOrganisation.getAbbreviationIt())
                                          .abbreviationEn(businessOrganisation.getAbbreviationEn())
                                          .validFrom(transportCompanyRelation.getValidFrom())
                                          .validTo(transportCompanyRelation.getValidTo()).build();
  }
}
