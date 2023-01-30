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
    List<ServicePointVersion> dbVersions = findServicePoint(edited.getNumber());
    ServicePointVersion current = getCurrentServicePointVersion(dbVersions, edited);
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsWithDeleteByNullProperties(current, edited,
        dbVersions);
    versionableService.applyVersioning(ServicePointVersion.class, versionedObjects, this::save, this::deleteById);
  }

  private ServicePointVersion getCurrentServicePointVersion(List<ServicePointVersion> dbVersions,
      ServicePointVersion edited) {
    // csvVersion = edited (param), dbVersions (sorted)
    Optional<ServicePointVersion> currentVersionMatch = dbVersions.stream()
        .filter(dbVersion -> {
              // match validFrom
              if (edited.getValidFrom().isEqual(dbVersion.getValidFrom())) {
                return true;
              }
              // match validTo
              if (edited.getValidTo().isEqual(dbVersion.getValidTo())) {
                return true;
              }
              // match csvVersion between dbVersion
              if (edited.getValidFrom().isAfter(dbVersion.getValidFrom()) && edited.getValidTo().isBefore(dbVersion.getValidTo())) {
                return true;
              }
              // match 1 or more dbVersion/s between csvVersion
              return dbVersion.getValidFrom().isAfter(edited.getValidFrom()) && dbVersion.getValidTo()
                  .isBefore(edited.getValidTo());
            }
        )
        .findFirst();

    if (currentVersionMatch.isEmpty()) {
      // match after version
      if (edited.getValidFrom().isAfter(dbVersions.get(dbVersions.size() - 1).getValidTo())) {
        return dbVersions.get(dbVersions.size() - 1);
      }
      // match before version
      if (edited.getValidTo().isBefore(dbVersions.get(0).getValidFrom())) {
        return dbVersions.get(0);
      }
    }

    return currentVersionMatch.orElseThrow(() -> new RuntimeException("Not found current version for update"));
  }

  private boolean isServicePointVersionValidToday(ServicePointVersion servicePointVersion) {
    LocalDate today = LocalDate.now();
    LocalDate validFrom = servicePointVersion.getValidFrom();
    LocalDate validTo = servicePointVersion.getValidTo();
    return (today.isAfter(validFrom) || today.isEqual(validFrom)) && (today.isBefore(validTo) || today.isEqual(validTo));
  }
}
