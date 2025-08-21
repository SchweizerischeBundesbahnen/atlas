package ch.sbb.business.organisation.directory.module.transportcompany.mapper;

import ch.sbb.atlas.api.bodi.TransportCompanyBoRelationModel;
import ch.sbb.business.organisation.directory.module.businessorganisation.entity.BusinessOrganisation;
import ch.sbb.business.organisation.directory.module.businessorganisation.mapper.BusinessOrganisationMapper;
import ch.sbb.business.organisation.directory.module.transportcompany.entity.TransportCompanyRelation;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TransportCompanyBoRelationMapper {

  public static TransportCompanyBoRelationModel toModel(BusinessOrganisation businessOrganisation,
      TransportCompanyRelation transportCompanyRelation) {
    return TransportCompanyBoRelationModel.builder()
        .id(transportCompanyRelation.getId())
        .businessOrganisation(BusinessOrganisationMapper.toModel(businessOrganisation))
        .validFrom(transportCompanyRelation.getValidFrom())
        .validTo(transportCompanyRelation.getValidTo()).build();
  }

}
