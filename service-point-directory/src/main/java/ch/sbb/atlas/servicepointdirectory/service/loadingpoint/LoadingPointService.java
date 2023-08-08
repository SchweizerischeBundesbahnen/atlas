package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.model.search.LoadingPointSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.repository.LoadingPointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.CrossValidationService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoadingPointService {

  private final LoadingPointVersionRepository loadingPointVersionRepository;
  private final CrossValidationService crossValidationService;

  public Page<LoadingPointVersion> findAll(LoadingPointSearchRestrictions searchRestrictions) {
    return loadingPointVersionRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public List<LoadingPointVersion> findLoadingPoint(ServicePointNumber servicePointNumber, Integer loadingPointNumber) {
    return loadingPointVersionRepository.findAllByServicePointNumberAndNumberOrderByValidFrom(servicePointNumber,
        loadingPointNumber);
  }

  public Optional<LoadingPointVersion> findById(Long id) {
    return loadingPointVersionRepository.findById(id);
  }

  public boolean isLoadingPointExisting(ServicePointNumber servicePointNumber, Integer loadingPointNumber) {
    return loadingPointVersionRepository.existsByServicePointNumberAndNumber(servicePointNumber, loadingPointNumber);
  }

  public LoadingPointVersion save(LoadingPointVersion loadingPointVersion) {
    crossValidationService.validateServicePointNumberExists(loadingPointVersion.getServicePointNumber());
    return loadingPointVersionRepository.save(loadingPointVersion);
  }

  public void deleteById(Long id) {
    loadingPointVersionRepository.deleteById(id);
  }
}
