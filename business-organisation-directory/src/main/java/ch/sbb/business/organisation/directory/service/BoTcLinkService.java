package ch.sbb.business.organisation.directory.service;

import ch.sbb.business.organisation.directory.entity.BoTcLink;
import ch.sbb.business.organisation.directory.exception.SboidNotFoundException;
import ch.sbb.business.organisation.directory.exception.TcIdNotFoundException;
import ch.sbb.business.organisation.directory.repository.BoTcLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoTcLinkService {

  private final BoTcLinkRepository boTcLinkRepository;

  private final BusinessOrganisationService businessOrganisationService;
  private final TransportCompanyService transportCompanyService;

  public BoTcLink save(BoTcLink entity){
    if (businessOrganisationService.findBusinessOrganisationVersions(entity.getSboid()).isEmpty()){
      throw new SboidNotFoundException(entity.getSboid());
    }
    if (transportCompanyService.findTransportCompanyById(entity.getTransportCompanyId().longValue()).isEmpty()){
      throw new TcIdNotFoundException(entity.getTransportCompanyId());
    }
    return boTcLinkRepository.save(entity);
  }

}
