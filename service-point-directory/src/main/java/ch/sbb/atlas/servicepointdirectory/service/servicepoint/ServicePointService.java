package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServicePointService {

  private final ServicePointVersionRepository servicePointVersionRepository;

  public Page<ServicePointVersion> findAll(Pageable pageable) {
    return servicePointVersionRepository.findAll(pageable);
  }

  public List<ServicePointVersion> findServicePointVersions(ServicePointNumber servicePointNumber) {
    return servicePointVersionRepository.findAllByNumber(servicePointNumber);
  }

  public Optional<ServicePointVersion> findById(Long id) {
    return servicePointVersionRepository.findById(id);
  }
}
