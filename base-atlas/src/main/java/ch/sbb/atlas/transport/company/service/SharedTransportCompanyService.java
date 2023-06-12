package ch.sbb.atlas.transport.company.service;

import ch.sbb.atlas.transport.company.entity.SharedTransportCompany;
import ch.sbb.atlas.transport.company.repository.SharedTransportCompanyRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SharedTransportCompanyService {

  private final SharedTransportCompanyRepository sharedTransportCompanyRepository;

  public Optional<SharedTransportCompany> findById(Long id) {
    return sharedTransportCompanyRepository.findById(id);
  }
}
