package ch.sbb.business.organisation.directory.module.transportcompany.controller;

import ch.sbb.atlas.api.bodi.TransportCompanyBoRelationModel;
import ch.sbb.atlas.api.bodi.TransportCompanyRelationApiInternal;
import ch.sbb.atlas.api.bodi.TransportCompanyRelationModel;
import ch.sbb.atlas.api.bodi.UpdateTransportCompanyRelationModel;
import ch.sbb.business.organisation.directory.module.businessorganisation.entity.BusinessOrganisation;
import ch.sbb.business.organisation.directory.module.businessorganisation.service.BusinessOrganisationService;
import ch.sbb.business.organisation.directory.module.transportcompany.entity.TransportCompany;
import ch.sbb.business.organisation.directory.module.transportcompany.entity.TransportCompanyRelation;
import ch.sbb.business.organisation.directory.module.transportcompany.exception.TransportCompanyNotFoundException;
import ch.sbb.business.organisation.directory.module.transportcompany.mapper.TransportCompanyBoRelationMapper;
import ch.sbb.business.organisation.directory.module.transportcompany.mapper.TransportCompanyRelationMapper;
import ch.sbb.business.organisation.directory.module.transportcompany.service.TransportCompanyRelationService;
import ch.sbb.business.organisation.directory.module.transportcompany.service.TransportCompanyService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TransportCompanyRelationControllerInternal implements TransportCompanyRelationApiInternal {

  private final TransportCompanyRelationService transportCompanyRelationService;
  private final TransportCompanyService transportCompanyService;
  private final BusinessOrganisationService businessOrganisationService;

  @Override
  public TransportCompanyBoRelationModel createTransportCompanyRelation(
      TransportCompanyRelationModel model) {
    TransportCompanyRelation relationEntity = TransportCompanyRelationMapper.toEntity(model,
        transportCompanyService.findById(model.getTransportCompanyId())
            .orElseThrow(() -> new TransportCompanyNotFoundException(
                model.getTransportCompanyId())));

    TransportCompanyRelation savedRelationEntity = transportCompanyRelationService.save(
        relationEntity, false);

    BusinessOrganisation businessOrganisation = businessOrganisationService.findBusinessOrganisationBySboid(
        savedRelationEntity.getSboid());

    return TransportCompanyBoRelationMapper.toModel(
        businessOrganisation,
        savedRelationEntity);
  }

  @Override
  public List<TransportCompanyBoRelationModel> getTransportCompanyRelations(
      Long transportCompanyId) {

    TransportCompany transportCompany = transportCompanyService.findById(transportCompanyId)
        .orElseThrow(
            () -> new TransportCompanyNotFoundException(
                transportCompanyId));

    return transportCompany.getTransportCompanyRelations()
        .stream()
        .map(
            transportCompanyRelation -> {
              BusinessOrganisation businessOrganisation = businessOrganisationService.findBusinessOrganisationBySboid(
                  transportCompanyRelation.getSboid());

              return TransportCompanyBoRelationMapper.toModel(
                  businessOrganisation,
                  transportCompanyRelation);
            })
        .collect(Collectors.toList());
  }

  @Override
  public void deleteTransportCompanyRelation(@PathVariable Long relationId) {
    transportCompanyRelationService.deleteById(relationId);
  }

  @Override
  public void updateTransportCompanyRelation(UpdateTransportCompanyRelationModel model) {
    TransportCompanyRelation relationEntity = transportCompanyRelationService.findById(model.getId());
    relationEntity.setValidFrom(model.getValidFrom());
    relationEntity.setValidTo(model.getValidTo());
    transportCompanyRelationService.save(relationEntity, true);
  }
}
