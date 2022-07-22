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

  @Schema(description = "Business Organisation")
  private BusinessOrganisationModel businessOrganisation;

  @Schema(description = "Valid From")
  private LocalDate validFrom;

  @Schema(description = "Valid To")
  private LocalDate validTo;

  public static TransportCompanyBoRelationModel toModel(
      BusinessOrganisation businessOrganisation,
      TransportCompanyRelation transportCompanyRelation) {
    return TransportCompanyBoRelationModel.builder()
                                          .id(transportCompanyRelation.getId())
                                          .businessOrganisation(BusinessOrganisationModel.toModel(
                                              businessOrganisation))
                                          .validFrom(transportCompanyRelation.getValidFrom())
                                          .validTo(transportCompanyRelation.getValidTo()).build();
  }
}
