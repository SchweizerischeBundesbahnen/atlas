package ch.sbb.business.organisation.directory.mapper;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TransportCompanyMapper {

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
