package ch.sbb.business.organisation.directory.controller;


import ch.sbb.business.organisation.directory.api.TransportCompanyRelationApiV1;
import ch.sbb.business.organisation.directory.api.TransportCompanyRelationModel;
import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation;
import ch.sbb.business.organisation.directory.service.TransportCompanyRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
public class TransportCompanyRelationController implements TransportCompanyRelationApiV1 {

  private final TransportCompanyRelationService transportCompanyRelationService;

  @Override
  public TransportCompanyRelationModel createTransportCompanyRelation(TransportCompanyRelationModel model){
    TransportCompanyRelation entity = TransportCompanyRelationModel.toEntity(model);
    TransportCompanyRelation savedEntity = transportCompanyRelationService.save(entity);
    return TransportCompanyRelationModel.toModel(savedEntity);
  }

}
