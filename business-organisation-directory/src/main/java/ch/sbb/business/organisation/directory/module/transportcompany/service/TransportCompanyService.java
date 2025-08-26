package ch.sbb.business.organisation.directory.module.transportcompany.service;

import ch.sbb.business.organisation.directory.module.transportcompany.entity.TransportCompany;
import ch.sbb.business.organisation.directory.module.transportcompany.model.TransportCompanySearchRestrictions;
import ch.sbb.business.organisation.directory.module.transportcompany.repository.TransportCompanyRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class TransportCompanyService {

  private final TransportCompanyRepository transportCompanyRepository;

  public Page<TransportCompany> getTransportCompanies(
      TransportCompanySearchRestrictions searchRestrictions) {
    return transportCompanyRepository.findAll(searchRestrictions.getSpecification(),
        searchRestrictions.getPageable());
  }

  public Optional<TransportCompany> findById(Long id) {
    return transportCompanyRepository.findById(id);
  }

  public boolean existsById(Long id) {
    return transportCompanyRepository.existsById(id);
  }

  public List<TransportCompany> findBySboid(String sboid) {
    return transportCompanyRepository.findAllWithSboid(sboid);
  }
}
