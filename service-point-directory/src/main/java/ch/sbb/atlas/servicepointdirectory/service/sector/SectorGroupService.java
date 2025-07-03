package ch.sbb.atlas.servicepointdirectory.service.sector;

import ch.sbb.atlas.api.servicepoint.sector.ReadSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.SectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.relation.SectorGroupRelationModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.exception.SectorNotExistingException;
import ch.sbb.atlas.servicepointdirectory.exception.SectorNotValidException;
import ch.sbb.atlas.servicepointdirectory.exception.SloidsNotEqualException;
import ch.sbb.atlas.servicepointdirectory.exception.TrafficPointNotFoundException;
import ch.sbb.atlas.servicepointdirectory.mapper.SectorGroupMapper;
import ch.sbb.atlas.servicepointdirectory.mapper.SectorGroupRelationMapper;
import ch.sbb.atlas.servicepointdirectory.mapper.SectorMapper;
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
    List<String> sectorSloids = findAllSectorsRelatedToGroup(sectorGroupVersion.getSloid());
    List<SectorVersionModel> sectorVersionModels = mapSectorVersions(sectorSloids);

    return SectorGroupMapper.toReadModel(sectorGroupVersion, sectorVersionModels);
  }

  private List<String> findAllSectorsRelatedToGroup(String sectorGroupSloid) {
    return sectorGroupRelationRepository.findBySectorGroupRelationIdSectorGroupSloid(sectorGroupSloid)
        .stream()
        .map(r -> r.getSectorGroupRelationId().getSectorSloid()).toList();
  }

  @Transactional
  public ReadSectorGroupVersionModel createSectorGroup(SectorGroupVersion sectorGroupVersionToCreate, List<String> sectorSloids) {
    List<SectorVersion> sectorVersions = sectorVersionRepository.findAllBySloidIn(sectorSloids);

    //TODO locationService.claimSloid waiting for -> ATLAS-2963 (LocationService erweiterung)

    preSaveCheck(sectorGroupVersionToCreate, sectorVersions);
    createRelation(sectorSloids, sectorGroupVersionToCreate.getSloid());
    SectorGroupVersion savedSectorGroupVersion = save(sectorGroupVersionToCreate);

    List<SectorVersionModel> sectorVersionModels = sectorVersions.stream().map(SectorMapper::toModel).toList();
    return SectorGroupMapper.toReadModel(savedSectorGroupVersion, sectorVersionModels);
  }

  private void preSaveCheck(SectorGroupVersion sectorGroupVersion, List<SectorVersion> sectorVersions) {
    existSectorVersion(sectorVersions);
    hasAtLeastTwoValidSectorVersions(sectorVersions);
    existTrafficPointElement(sectorGroupVersion.getTrafficPointSloid());
    isTrafficPointSloidMatchingOverAllObjects(sectorVersions, sectorGroupVersion.getTrafficPointSloid());
  }

  private List<SectorVersionModel> mapSectorVersions(List<String> sectorSloids) {
    List<SectorVersionModel> sectorVersionModels = new ArrayList<>();
    for (String sectorSloid : sectorSloids) {
      SectorVersion sectorVersion = sectorVersionRepository.findAllBySloidOrderByValidFrom(sectorSloid).getFirst();
      sectorVersionModels.add(SectorMapper.toModel(sectorVersion));
    }
    return sectorVersionModels;
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
      throw new TrafficPointNotFoundException(sloid);
    }
  }

  private void existSectorVersion(List<SectorVersion> sectorVersions) {
    if (sectorVersions.isEmpty()) {
      throw new SectorNotExistingException(sectorVersions.getFirst().getSloid());
    }
  }

  private void isTrafficPointSloidMatchingOverAllObjects(List<SectorVersion> sectorVersions, String trafficPointSloid) {
    sectorVersions.forEach(sectorVersion -> {
      if (!sectorVersion.getTrafficPointSloid().equals(trafficPointSloid)) {
        throw new SloidsNotEqualException("Traffic Point sloid of sector not matching with sector group traffic point sloid");
      }
    });
  }

  private void hasAtLeastTwoValidSectorVersions(List<SectorVersion> sectorVersions) {
    if (sectorVersions.size() < 2) {
      throw new SectorNotValidException("Should be at least two valid sector versions");
    }
  }

  private SectorGroupVersion save(SectorGroupVersion sectorGroupVersion) {
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
