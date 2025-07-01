package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.api.servicepoint.CreateSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.SectorVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepointdirectory.entity.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.entity.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.mapper.SectorGroupMapper;
import ch.sbb.atlas.servicepointdirectory.mapper.SectorMapper;
import ch.sbb.atlas.servicepointdirectory.repository.SectorGroupVersionRepository;
import ch.sbb.atlas.servicepointdirectory.repository.SectorVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementService;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class SectorGroupService {

  private final SectorGroupVersionRepository sectorGroupVersionRepository;
  private final SectorVersionRepository sectorVersionRepository;
  private final TrafficPointElementService trafficPointElementService;
  private final VersionableService versionableService;

  public SectorGroupService(SectorGroupVersionRepository sectorGroupVersionRepository,
      SectorVersionRepository sectorVersionRepository, TrafficPointElementService trafficPointElementService,
      VersionableService versionableService) {
    this.sectorGroupVersionRepository = sectorGroupVersionRepository;
    this.sectorVersionRepository = sectorVersionRepository;
    this.trafficPointElementService = trafficPointElementService;
    this.versionableService = versionableService;
  }

  public List<SectorGroupVersion> getSectorGroups() {
    return sectorGroupVersionRepository.findAll();
  }

  public ReadSectorGroupVersionModel createSectorGroup(CreateSectorGroupVersionModel createSectorGroupVersionModel) {
    SectorGroupVersion sectorGroupVersion = SectorGroupMapper.toEntity(createSectorGroupVersionModel);
    List<SectorVersion> validSectorVersions = new ArrayList<>();
    existTrafficPointElement(createSectorGroupVersionModel.getTrafficPointSloid());

    List<SectorVersion> sectorVersions =
        sectorVersionRepository.findAllBySloidIn(createSectorGroupVersionModel.getSectorSloids());

    existSectorVersion(sectorVersions);
    isTrafficPointSloidMatchingOverAllObjects(sectorVersions, sectorGroupVersion.getTrafficPointSloid(), validSectorVersions);
    hasAtLeastTwoValidSectorVersions(validSectorVersions);

    sectorGroupVersion.setSectorVersions(sectorVersions);

    SectorGroupVersion savedSectorGroupVersion = save(sectorGroupVersion);
    List<SectorVersionModel> sectorVersionModelList =
        SectorMapper.toModelFromList(savedSectorGroupVersion.getSectorVersions());
    return SectorGroupMapper.toModelWithSectorVersions(savedSectorGroupVersion, sectorVersionModelList);
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

  public void updateSectorGroup(SectorGroupVersion currentVersion, SectorGroupVersion editedVersion) {
    System.out.println("test");
    sectorGroupVersionRepository.incrementVersion(currentVersion.getSloid());

    if (!currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(ServicePointVersion.class.getSimpleName(), "version");
    }

    editedVersion.setSloid(currentVersion.getSloid());
    editedVersion.setTrafficPointSloid(currentVersion.getTrafficPointSloid());
    editedVersion.setSectorVersions(currentVersion.getSectorVersions());

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

}
