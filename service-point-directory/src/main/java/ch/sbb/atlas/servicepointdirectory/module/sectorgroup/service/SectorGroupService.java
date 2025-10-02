package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.service;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.sector.ReadSectorVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.SectorGroupVersionModel;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.service.OverviewDisplayBuilder;
import ch.sbb.atlas.servicepointdirectory.exception.SloidsNotEqualException;
import ch.sbb.atlas.servicepointdirectory.module.sector.entity.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.module.sector.exception.AtLeastTwoSectorsRequiredException;
import ch.sbb.atlas.servicepointdirectory.module.sector.exception.SectorNotExistingException;
import ch.sbb.atlas.servicepointdirectory.module.sector.mapper.SectorMapper;
import ch.sbb.atlas.servicepointdirectory.module.sector.repository.SectorVersionRepository;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.mapper.SectorGroupMapper;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.model.SectorGroupRelationId;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.repository.SectorGroupRelationRepository;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.repository.SectorGroupVersionRepository;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.mapper.SectorGroupRelationMapper;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.service.TrafficPointElementService;
import ch.sbb.atlas.servicepointdirectory.service.SectorValidationService;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SectorGroupService {

  private final SectorGroupVersionRepository sectorGroupVersionRepository;
  private final TrafficPointElementService trafficPointElementService;
  private final VersionableService versionableService;
  private final SectorGroupRelationRepository sectorGroupRelationRepository;
  private final SectorVersionRepository sectorVersionRepository;
  private final LocationService locationService;
  private final SectorValidationService sectorValidationService;

  @Transactional
  @PreAuthorize("""
      @countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreateOrEditServicePointDependentObject
      (#servicePointVersions,T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)""")
  public SectorGroupVersionModel create(SectorGroupVersion sectorGroupVersion,
      List<String> sloids, List<ServicePointVersion> servicePointVersions) {
    return createSectorGroup(sectorGroupVersion, sloids, servicePointVersions);
  }

  public SectorGroupVersionModel createSectorGroup(SectorGroupVersion sectorGroupVersion, List<String> sloids,
      List<ServicePointVersion> servicePointVersions) {

    trafficPointElementService.doesTrafficPointExist(sectorGroupVersion.getTrafficPointSloid());
    List<SectorVersion> versions = fetchLatestSectorVersions(sloids);

    validateSectorVersions(versions);
    validateTrafficPoint(sectorGroupVersion.getTrafficPointSloid(), versions);

    sectorValidationService.validateValidity(sectorGroupVersion);
    sectorValidationService.validateMeanOfTransportOfServicePoint(servicePointVersions);

    sectorGroupVersion.setSloid(locationService.generateSloid(SloidType.SECTOR_GROUP, sectorGroupVersion.getTrafficPointSloid()));

    createRelation(sloids, sectorGroupVersion.getSloid());
    SectorGroupVersion saved = save(sectorGroupVersion);

    return SectorGroupMapper.toModel(saved);
  }

  private List<SectorVersion> fetchLatestSectorVersions(List<String> sloids) {
    return sloids.stream()
        .map(sloid -> {
          List<SectorVersion> sectorVersions = sectorVersionRepository
              .findAllBySloidOrderByValidFrom(sloid);
          if (sectorVersions.isEmpty()) {
            throw new SectorNotExistingException(sloid);
          }
          return sectorVersions.getLast();
        })
        .toList();
  }

  private void validateSectorVersions(List<SectorVersion> versions) {
    if (versions.size() < 2) {
      throw new AtLeastTwoSectorsRequiredException();
    }
  }

  private void validateTrafficPoint(String trafficPointSloid, List<SectorVersion> sectorVersions) {
    trafficPointElementService.doesTrafficPointExist(trafficPointSloid);
    isTrafficPointSloidMatchingOverAllObjects(sectorVersions, trafficPointSloid);
  }

  private List<ReadSectorVersionModel> mapToModels(List<SectorVersion> versions) {
    return versions.stream()
        .map(SectorMapper::toModel)
        .toList();
  }

  private void createRelation(List<String> sectorSloids, String sectorGroupSloid) {
    for (String sectorSloid : sectorSloids) {

      SectorGroupRelationId sectorGroupRelationModel = SectorGroupRelationId.builder()
          .sectorGroupSloid(sectorGroupSloid)
          .sectorSloid(sectorSloid)
          .build();

      sectorGroupRelationRepository.saveAndFlush(SectorGroupRelationMapper.toEntity(sectorGroupRelationModel));
    }
  }

  private void isTrafficPointSloidMatchingOverAllObjects(List<SectorVersion> sectorVersions, String trafficPointSloid) {
    sectorVersions.forEach(sectorVersion -> {
      if (!sectorVersion.getTrafficPointSloid().equals(trafficPointSloid)) {
        throw new SloidsNotEqualException("Traffic Point sloid of sector not matching with sector group traffic point sloid");
      }
    });
  }

  private SectorGroupVersion save(SectorGroupVersion sectorGroupVersion) {
    return sectorGroupVersionRepository.saveAndFlush(sectorGroupVersion);
  }

  @Transactional
  @PreAuthorize("""
      @countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreateOrEditServicePointDependentObject
      (#servicePointVersions,T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)""")
  public void update(SectorGroupVersion currentVersion, SectorGroupVersion editedVersion,
      List<ServicePointVersion> servicePointVersions) {
    updateSectorGroup(currentVersion, editedVersion);
  }

  public void updateSectorGroup(SectorGroupVersion currentVersion, SectorGroupVersion editedVersion) {
    sectorGroupVersionRepository.incrementVersion(currentVersion.getSloid());

    if (!currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(ServicePointVersion.class.getSimpleName(), "version");
    }

    editedVersion.setSloid(currentVersion.getSloid());
    editedVersion.setTrafficPointSloid(currentVersion.getTrafficPointSloid());

    List<SectorGroupVersion> currentVersions = findAllBySloidOrderByValidFrom(currentVersion.getSloid());

    sectorValidationService.validateValidity(editedVersion);

    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion,
        currentVersions);

    versionableService.applyVersioning(SectorGroupVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(sectorGroupVersionRepository));
  }

  public SectorGroupVersion getSectorGroupVersionById(Long id) {
    return sectorGroupVersionRepository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public List<SectorGroupVersion> findAllBySloidOrderByValidFrom(String sectorGroupSloid) {
    List<SectorGroupVersion> sectorGroupVersions = sectorGroupVersionRepository.findAllBySloidOrderByValidFrom(sectorGroupSloid);
    if (sectorGroupVersions.isEmpty()) {
      throw new SloidNotFoundException(sectorGroupSloid);
    }
    return sectorGroupVersions;
  }

  public Container<SectorGroupVersionModel> getSectorGroupsOfTrafficPoint(String trafficPointSloid, Pageable pageable) {
    List<SectorGroupVersion> sectorGroupVersions = sectorGroupVersionRepository.findAllByTrafficPointSloid(trafficPointSloid,
        pageable.getSort());

    List<SectorGroupVersionModel> overviewModels = sectorGroupVersions.stream().map(SectorGroupMapper::toModel).toList();
    List<SectorGroupVersionModel> displayableModels = OverviewDisplayBuilder.mergeVersionsForDisplay(overviewModels,
        SectorGroupVersionModel::getSloid);
    return OverviewDisplayBuilder.toPagedContainer(displayableModels, pageable);
  }

  public List<ReadSectorVersionModel> getSectorsBySectorGroupSloid(String sectorGroupSloid) {
    List<String> sectorSloids = findAllSectorsRelatedToGroup(sectorGroupSloid);

    List<SectorVersion> sectors = sectorSloids.stream()
        .map(sectorSloid -> {
          List<SectorVersion> sectorVersions = sectorVersionRepository.findAllBySloidOrderByValidFrom(sectorSloid);

          return OverviewDisplayBuilder.mergeVersionsForDisplay(
              sectorVersions,
              SectorVersion::getSloid
          );
        })
        .flatMap(List::stream)
        .toList();

    return mapToModels(sectors);
  }

  private List<String> findAllSectorsRelatedToGroup(String sectorGroupSloid) {
    return sectorGroupRelationRepository.findBySectorGroupRelationIdSectorGroupSloid(sectorGroupSloid)
        .stream()
        .map(r -> r.getSectorGroupRelationId().getSectorSloid()).toList();
  }

}
