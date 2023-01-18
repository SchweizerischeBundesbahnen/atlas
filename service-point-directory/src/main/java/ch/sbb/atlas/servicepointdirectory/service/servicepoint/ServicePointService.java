package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.base.service.versioning.model.VersionedObject;
import ch.sbb.atlas.base.service.versioning.service.VersionableService;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.model.search.ServicePointSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServicePointService {

  private final ServicePointVersionRepository servicePointVersionRepository;
  private final VersionableService versionableService;

  public Page<ServicePointVersion> findAll(ServicePointSearchRestrictions servicePointSearchRestrictions) {
    return servicePointVersionRepository.findAll(servicePointSearchRestrictions.getSpecification(),
        servicePointSearchRestrictions.getPageable());
  }

  public List<ServicePointVersion> findServicePoint(ServicePointNumber servicePointNumber) {
    return servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
  }

  public boolean isServicePointNumberExisting(ServicePointNumber servicePointNumber) {
    return findServicePoint(servicePointNumber).size() > 0;
  }

  public Optional<ServicePointVersion> findById(Long id) {
    return servicePointVersionRepository.findById(id);
  }

  public void deleteById(Long id) {
    servicePointVersionRepository.deleteById(id);
  }

  public ServicePointVersion save(ServicePointVersion servicePointVersion) {
    return servicePointVersionRepository.save(servicePointVersion);
  }

  public void updateServicePointVersion(ServicePointVersion edited) {
    List<ServicePointVersion> currentVersions = findServicePoint(edited.getNumber());
    ServicePointVersion current = getCurrentServicePointVersion(currentVersions);
    List<VersionedObject> versionedObjects = versionableService.versioningObjects(current, edited, currentVersions);
    versionableService.applyVersioning(ServicePointVersion.class, versionedObjects, this::save, this::deleteById);
  }

  private ServicePointVersion getCurrentServicePointVersion(List<ServicePointVersion> servicePointVersions) {
    List<ServicePointVersion> currentServicePoints = servicePointVersions.stream().filter(this::isServicePointVersionValidToday)
        .toList();
    if (currentServicePoints.size() != 1) {
      throw new RuntimeException("Did not found currentServicePoint");
    }
    return currentServicePoints.get(0);
  }

  private boolean isServicePointVersionValidToday(ServicePointVersion servicePointVersion) {
    LocalDate today = LocalDate.now();
    LocalDate validFrom = servicePointVersion.getValidFrom();
    LocalDate validTo = servicePointVersion.getValidTo();
    return (today.isAfter(validFrom) || today.isEqual(validFrom)) && (today.isBefore(validTo) || today.isEqual(validTo));
  }
}
