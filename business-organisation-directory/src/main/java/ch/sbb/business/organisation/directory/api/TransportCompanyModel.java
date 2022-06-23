package ch.sbb.business.organisation.directory.api;

import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.service.TransportCompanyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "TransportCompany")
public class TransportCompanyModel {

  @Schema(description = "Identifier")
  private Long id;

  @Schema(description = "Number")
  private String number;

  @Schema(description = "Abbreviation")
  private String abbreviation;

  @Schema(description = "Description")
  private String description;

  @Schema(description = "Business Register Name")
  private String businessRegisterName;

  @Schema(description = "Status")
  private TransportCompanyStatus transportCompanyStatus;

  @Schema(description = "Business Register Number")
  private String businessRegisterNumber;

  @Schema(description = "Enterprise ID")
  private String enterpriseId;

  @Schema(description = "RICS Code")
  private String ricsCode;

  @Schema(description = "Business Organisation Numbers")
  private String businessOrganisationNumbers;

  @Schema(description = "Comment")
  private String comment;

  public static TransportCompanyModel fromEntity(TransportCompany entity) {
    return TransportCompanyModel.builder()
                                .id(entity.getId())
                                .number(entity.getNumber())
                                .abbreviation(entity.getAbbreviation())
                                .description(entity.getDescription())
                                .businessRegisterName(entity.getBusinessRegisterName())
                                .transportCompanyStatus(entity.getTransportCompanyStatus())
                                .businessRegisterNumber(entity.getBusinessRegisterNumber())
                                .enterpriseId(entity.getEnterpriseId())
                                .ricsCode(entity.getRicsCode())
                                .businessOrganisationNumbers(
                                    entity.getBusinessOrganisationNumbers())
                                .comment(entity.getComment())
                                .build();
  }
}
