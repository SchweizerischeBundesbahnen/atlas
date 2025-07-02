package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.api.servicepoint.CreateSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.SectorGroupRelationModel;
import ch.sbb.atlas.api.servicepoint.SectorGroupVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepointdirectory.entity.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.entity.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.mapper.SectorGroupMapper;
import ch.sbb.atlas.servicepointdirectory.mapper.SectorGroupRelationMapper;
import ch.sbb.atlas.servicepointdirectory.repository.SectorGroupRelationRepository;
import ch.sbb.atlas.servicepointdirectory.repository.SectorGroupVersionRepository;
import ch.sbb.atlas.servicepointdirectory.repository.SectorVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementService;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class SectorGroupService {

  private final SectorGroupVersionRepository sectorGroupVersionRepository;
  private final TrafficPointElementService trafficPointElementService;
  private final VersionableService versionableService;
  private final SectorGroupRelationRepository sectorGroupRelationRepository;
  private final SectorVersionRepository sectorVersionRepository;

  public SectorGroupService(SectorGroupVersionRepository sectorGroupVersionRepository,
      TrafficPointElementService trafficPointElementService,
      VersionableService versionableService, SectorGroupRelationRepository sectorGroupRelationRepository,
      SectorVersionRepository sectorVersionRepository) {
    this.sectorGroupVersionRepository = sectorGroupVersionRepository;
    this.trafficPointElementService = trafficPointElementService;
    this.versionableService = versionableService;
    this.sectorGroupRelationRepository = sectorGroupRelationRepository;
    this.sectorVersionRepository = sectorVersionRepository;
  }

  public List<SectorGroupVersionModel> getSectorGroups() {
    return sectorGroupVersionRepository.findAll().stream().map(SectorGroupMapper::toModel).toList();
  }

  public List<SectorGroupVersionModel> getSectorGroup(String sectorGroupSloid) {
    List<SectorGroupVersion> sectorGroupVersions = findAllBySloidOrderByValidFrom(sectorGroupSloid);
    return sectorGroupVersions.stream().map(SectorGroupMapper::toModel).toList();
  }

  public ReadSectorGroupVersionModel getSectorGroupVersion(Long id) {
    SectorGroupVersion sectorGroupVersion = sectorGroupVersionRepository.findById(id).orElseThrow();
    List<String> sectors = findAllSectorsRelatedToGroup(sectorGroupVersion.getSloid());

    return SectorGroupMapper.toReadModel(sectorGroupVersion, sectors);
  }

  private List<String> findAllSectorsRelatedToGroup(String sectorGroupSloid) {
    return sectorGroupRelationRepository.findBySectorGroupRelationIdSectorGroupSloid(sectorGroupSloid)
        .stream()
        .map(r -> r.getSectorGroupRelationId().getSectorSloid()).toList();
  }

  @Transactional
  public ReadSectorGroupVersionModel createSectorGroup(CreateSectorGroupVersionModel createSectorGroupVersionModel) {
    SectorGroupVersion sectorGroupVersion = SectorGroupMapper.toEntity(createSectorGroupVersionModel);
    List<String> sectorSloids = createSectorGroupVersionModel.getSectorSloids().stream().toList();
    String sectorGroupSloid = createSectorGroupVersionModel.getSloid();

    List<SectorVersion> validSectorVersions = new ArrayList<>();
    existTrafficPointElement(createSectorGroupVersionModel.getTrafficPointSloid());

    //TODO locationService.claimSloid waiting for -> ATLAS-2963 (LocationService erweiterung)

    List<SectorVersion> sectorVersions = sectorVersionRepository.findAllBySloidIn(sectorSloids);

    existSectorVersion(sectorVersions);
    isTrafficPointSloidMatchingOverAllObjects(sectorVersions, sectorGroupVersion.getTrafficPointSloid(), validSectorVersions);
    hasAtLeastTwoValidSectorVersions(validSectorVersions);

    createRelation(sectorSloids, sectorGroupSloid);
    SectorGroupVersion savedSectorGroupVersion = save(sectorGroupVersion);
    return SectorGroupMapper.toReadModel(savedSectorGroupVersion, sectorSloids);
  }

  private void createRelation(List<String> sectorSloids, String sectorGroupSloid) {
    for (String sectorSloid : sectorSloids) {
      SectorGroupRelationModel sectorGroupRelationModel = SectorGroupRelationModel.builder()
          .sectorGroupSloid(sectorGroupSloid)
          .sectorSloid(sectorSloid)
          .build();
      sectorGroupRelationRepository.saveAndFlush(SectorGroupRelationMapper.toEntity(sectorGroupRelationModel));
    }
  }

  private void existTrafficPointElement(String sloid) {
    if (trafficPointElementService.findBySloidOrderByValidFrom(sloid).isEmpty()) {
      //TODO change exception
      throw new RuntimeException("Traffic point not found");
    }
  }

  private void existSectorVersion(List<SectorVersion> sectorVersions) {
    if (sectorVersions.isEmpty()) {
      //TODO change exception
      throw new RuntimeException("No sector version found");
    }
  }

  private void isTrafficPointSloidMatchingOverAllObjects(List<SectorVersion> sectorVersions, String trafficPointSloid,
      List<SectorVersion> validSectorVersions) {
    sectorVersions.forEach(sectorVersion -> {
      if (!sectorVersion.getTrafficPointSloid().equals(trafficPointSloid)) {
        //TODO change exception
        throw new RuntimeException("Traffic Point sloid of sector not matching with sector group traffic point sloid");

      } else {
        validSectorVersions.add(sectorVersion);
      }
    });
  }

  private void hasAtLeastTwoValidSectorVersions(List<SectorVersion> validSectorVersions) {
    if (validSectorVersions.size() < 2) {
      //TODO Change exception
      throw new RuntimeException("Should be at least two sector versions");
    }
  }

  SectorGroupVersion save(SectorGroupVersion sectorGroupVersion) {
    return sectorGroupVersionRepository.saveAndFlush(sectorGroupVersion);
  }

  @Transactional
  public void updateSectorGroup(SectorGroupVersion currentVersion, SectorGroupVersion editedVersion) {
    sectorGroupVersionRepository.incrementVersion(currentVersion.getSloid());

    if (!currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(ServicePointVersion.class.getSimpleName(), "version");
    }

    editedVersion.setSloid(currentVersion.getSloid());
    editedVersion.setTrafficPointSloid(currentVersion.getTrafficPointSloid());

    List<SectorGroupVersion> currentVersions = findAllBySloidOrderByValidFrom(currentVersion.getSloid());

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
    return sectorGroupVersionRepository.findAllBySloidOrderByValidFrom(sectorGroupSloid);
  }

  public Optional<SectorGroupVersion> findById(Long id) {
    return sectorGroupVersionRepository.findById(id);
  }

}
