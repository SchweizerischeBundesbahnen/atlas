package ch.sbb.business.organisation.directory.service;

import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation;
import ch.sbb.business.organisation.directory.exception.SboidNotFoundException;
import ch.sbb.business.organisation.directory.exception.TransportCompanyNotFoundException;
import ch.sbb.business.organisation.directory.repository.TransportCompanyRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransportCompanyRelationService {

  private final TransportCompanyRelationRepository transportCompanyRelationRepository;

  private final BusinessOrganisationService businessOrganisationService;
  private final TransportCompanyService transportCompanyService;

  public TransportCompanyRelation save(TransportCompanyRelation entity){
    if (businessOrganisationService.findBusinessOrganisationVersions(entity.getSboid()).isEmpty()){
      throw new SboidNotFoundException(entity.getSboid());
    }
    if (transportCompanyService.findById(entity.getTransportCompanyId()).isEmpty()){
      throw new TransportCompanyNotFoundException(entity.getTransportCompanyId());
    }
    return transportCompanyRelationRepository.save(entity);
  }

}
