package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.ReadTrafficPointElementVersionModel;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.exception.SloidAlreadyExistsException;
import ch.sbb.atlas.servicepointdirectory.mapper.TrafficPointElementVersionMapper;
import ch.sbb.atlas.servicepointdirectory.model.search.TrafficPointElementSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.CrossValidationService;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Getter
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TrafficPointElementService {

  private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;
  private final VersionableService versionableService;
  private final CrossValidationService crossValidationService;
  private final TrafficPointElementSloidService trafficPointElementSloidService;

  public Page<TrafficPointElementVersion> findAll(TrafficPointElementSearchRestrictions searchRestrictions) {
    return trafficPointElementVersionRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public List<TrafficPointElementVersion> findBySloidOrderByValidFrom(String sloid) {
    return trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(sloid);
  }

  public Optional<TrafficPointElementVersion> findById(Long id) {
    return trafficPointElementVersionRepository.findById(id);
  }

  public boolean isTrafficPointElementExisting(String sloid) {
    return trafficPointElementVersionRepository.existsBySloid(sloid);
  }

  @PreAuthorize("""
      @countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreateOrEditServicePointDependentObject
      (#servicePointVersions,T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)""")
  public TrafficPointElementVersion create(TrafficPointElementVersion trafficPointElementVersion,
      List<ServicePointVersion> servicePointVersions) {
    if (trafficPointElementVersion.getSloid() != null && isTrafficPointElementExisting(trafficPointElementVersion.getSloid())) {
      throw new SloidAlreadyExistsException(trafficPointElementVersion.getSloid());
    }
    if (trafficPointElementVersion.getSloid() == null
        && trafficPointElementVersion.getTrafficPointElementType() == TrafficPointElementType.BOARDING_PLATFORM) {
      trafficPointElementVersion.setSloid(
          trafficPointElementSloidService.getNextSloidForPlatform(trafficPointElementVersion.getServicePointNumber()));
    }
    return save(trafficPointElementVersion);
  }

  public TrafficPointElementVersion save(TrafficPointElementVersion trafficPointElementVersion) {
    crossValidationService.validateServicePointNumberExists(trafficPointElementVersion.getServicePointNumber());
    return trafficPointElementVersionRepository.saveAndFlush(trafficPointElementVersion);
  }

  @PreAuthorize("""
      @countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreateOrEditServicePointDependentObject
      (#currentVersions, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)""")
  public void update(TrafficPointElementVersion currentVersion, TrafficPointElementVersion editedVersion,
      List<ServicePointVersion> currentVersions) {
    updateTrafficPointElementVersion(currentVersion, editedVersion);
  }

  public void updateTrafficPointElementVersion(TrafficPointElementVersion currentVersion,
      TrafficPointElementVersion editedVersion) {
    trafficPointElementVersionRepository.incrementVersion(currentVersion.getSloid());
    if (editedVersion.getVersion() != null && !currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(ServicePointVersion.class.getSimpleName(), "version");
    }

    editedVersion.setServicePointNumber(currentVersion.getServicePointNumber());
    editedVersion.setSloid(currentVersion.getSloid());

    List<TrafficPointElementVersion> dbVersions = findBySloidOrderByValidFrom(currentVersion.getSloid());
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion,
        dbVersions);
    versionableService.applyVersioning(TrafficPointElementVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(trafficPointElementVersionRepository));
  }

  public Container<ReadTrafficPointElementVersionModel> getTrafficPointElementsByServicePointNumber(
      Integer servicePointNumber, Pageable pageable, TrafficPointElementType trafficPointElementType) {

    TrafficPointElementSearchRestrictions trafficPointElementSearchRestrictions = TrafficPointElementSearchRestrictions.builder()
        .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
            .trafficPointElementType(trafficPointElementType)
            .servicePointNumbers(List.of(String.valueOf(servicePointNumber)))
            .build())
        .build();

    List<TrafficPointElementVersion> trafficPointElementVersions = trafficPointElementVersionRepository.findAll(
        trafficPointElementSearchRestrictions.getSpecification(), pageable.getSort());

    List<ReadTrafficPointElementVersionModel> trafficPointElements = trafficPointElementVersions.stream()
        .map(TrafficPointElementVersionMapper::toModel).toList();

    List<ReadTrafficPointElementVersionModel> displayableVersions = mergeVersionsForDisplay(trafficPointElements);
    return Container.<ReadTrafficPointElementVersionModel>builder()
        .objects(displayableVersions.stream()
            .skip(Math.multiplyFull(pageable.getPageNumber(), pageable.getPageSize()))
            .limit(pageable.getPageSize())
            .toList())
        .totalCount(displayableVersions.size())
        .build();
  }

  private List<ReadTrafficPointElementVersionModel> mergeVersionsForDisplay(List<ReadTrafficPointElementVersionModel> trafficPointElements) {
    if (trafficPointElements.isEmpty()) {
      return Collections.emptyList();
    }

    List<ReadTrafficPointElementVersionModel> result = new ArrayList<>();

    List<ReadTrafficPointElementVersionModel> versions = new ArrayList<>();
    Iterator<ReadTrafficPointElementVersionModel> iterator = trafficPointElements.iterator();
    ReadTrafficPointElementVersionModel previous = iterator.next();
    versions.add(previous);
    ReadTrafficPointElementVersionModel current;

    while (iterator.hasNext()) {
      current = iterator.next();
      if (previous.getSloid().equals(current.getSloid())) {
        versions.add(current);
      }else {
        result.add(getDisplayModel(versions));
        versions.clear();
        versions.add(current);
      }
      previous = current;
    }
    result.add(getDisplayModel(versions));

    return result;
  }

  private <T extends Versionable> T getDisplayModel(List<T> versions) {
    List<T> sortedVersions = versions.stream().sorted(Comparator.comparing(T::getValidFrom)).toList();

    T versionToShow = getPrioritizedVersion(sortedVersions);
    versionToShow.setValidFrom(sortedVersions.get(0).getValidFrom());
    versionToShow.setValidTo(sortedVersions.get(sortedVersions.size() - 1).getValidTo());
    return versionToShow;
  }

  private <T extends Versionable> T getPrioritizedVersion(List<T> versions) {
    Optional<T> validToday = versions.stream().filter(i -> DateRange.fromVersionable(i).contains(LocalDate.now())).findFirst();
    Optional<T> validInFuture = versions.stream().filter(i -> i.getValidFrom().isAfter(LocalDate.now())).findFirst();
    return validToday.orElse(validInFuture.orElse(versions.get(versions.size() - 1)));
  }

  public List<TrafficPointElementVersion> getTrafficPointElementsByServicePointNumber(Integer servicePointNumber,
      LocalDate validOn) {
    return trafficPointElementVersionRepository.findAll(TrafficPointElementSearchRestrictions.builder()
        .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
            .servicePointNumbers(List.of(String.valueOf(servicePointNumber)))
            .validOn(validOn)
            .build())
        .build().getSpecification());
  }
}
