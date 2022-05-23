package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.model.Status;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BusinessOrganisationVersionService {

  private final BusinessOrganisationRepository repository;

  public List<BusinessOrganisationVersion> getBusinessOrganisations(){
    return repository.findAll();
  }

  public BusinessOrganisationVersion save(BusinessOrganisationVersion version){
    version.setStatus(Status.ACTIVE);
    return repository.save(version);
  }

}
