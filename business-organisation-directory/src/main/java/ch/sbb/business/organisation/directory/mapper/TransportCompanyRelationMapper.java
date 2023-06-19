package ch.sbb.business.organisation.directory.mapper;

import ch.sbb.atlas.api.bodi.TransportCompanyRelationModel;
import ch.sbb.atlas.api.bodi.UpdateTransportCompanyRelationModel;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TransportCompanyRelationMapper {

  public static TransportCompanyRelation toEntity(TransportCompanyRelationModel model, TransportCompany transportCompany) {
    return TransportCompanyRelation.builder()
        .sboid(model.getSboid())
        .transportCompany(transportCompany)
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo()).build();
  }

  public static TransportCompanyRelation toUpdateEntity(UpdateTransportCompanyRelationModel model, TransportCompany transportCompany) {
    return TransportCompanyRelation.builder()
        .id(model.getId())
        .sboid(model.getSboid())
        .transportCompany(transportCompany)
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo()).build();
  }

  public static TransportCompanyRelationModel toModel(TransportCompanyRelation entity) {
    return TransportCompanyRelationModel.builder()
        .transportCompanyId(entity.getTransportCompany().getId())
        .sboid(entity.getSboid())
        .validFrom(entity.getValidFrom())
        .validTo(entity.getValidTo()).build();
  }

}
