package ch.sbb.prm.directory.service;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PARKING_LOT;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.model.parkinglot.ReadParkingLotVersionModel;
import ch.sbb.atlas.service.OverviewService;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.mapper.ParkingLotVersionMapper;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.search.ParkingLotSearchRestrictions;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ParkingLotService extends PrmRelatableVersionableService<ParkingLotVersion> {

  private final ParkingLotRepository parkingLotRepository;

  public ParkingLotService(ParkingLotRepository parkingLotRepository, StopPointService stopPointService,
      RelationService relationService, ReferencePointRepository referencePointRepository,
      VersionableService versionableService, PrmLocationService locationService) {
    super(versionableService, stopPointService, relationService, referencePointRepository, locationService);
    this.parkingLotRepository = parkingLotRepository;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return PARKING_LOT;
  }

  @Override
  protected SloidType getSloidType() {
    return SloidType.PARKING_LOT;
  }

  @Override
  public void incrementVersion(String sloid) {
    parkingLotRepository.incrementVersion(sloid);
  }

  @Override
  protected ParkingLotVersion save(ParkingLotVersion version) {
    return parkingLotRepository.saveAndFlush(version);
  }

  @Override
  public List<ParkingLotVersion> getAllVersions(String sloid) {
    return parkingLotRepository.findAllBySloidOrderByValidFrom(sloid);
  }

  @Override
  protected void applyVersioning(List<VersionedObject> versionedObjects) {
    versionableService.applyVersioning(ParkingLotVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(parkingLotRepository));
  }

  public List<ParkingLotVersion> getAllParkingLots() {
    return parkingLotRepository.findAll();
  }

  @PreAuthorize("@prmUserAdministrationService.hasUserRightsToCreateOrEditPrmObject(#version)")
  public ParkingLotVersion createParkingLot(ParkingLotVersion version) {
    createRelationWithSloidAllocation(version);
    return save(version);
  }

  @PreAuthorize("@prmUserAdministrationService.hasUserRightsToCreateOrEditPrmObject(#editedVersion)")
  public ParkingLotVersion updateParkingLotVersion(ParkingLotVersion currentVersion, ParkingLotVersion editedVersion) {
    return updateVersion(currentVersion, editedVersion);
  }

  public Optional<ParkingLotVersion> getPlatformVersionById(Long id) {
    return parkingLotRepository.findById(id);
  }

  public void saveForImport(ParkingLotVersion version) {
    stopPointService.checkStopPointExists(version.getParentServicePointSloid());
    parkingLotRepository.saveAndFlush(version);
  }

  public ParkingLotVersion createParkingLotThroughImport(ParkingLotVersion version) {
    stopPointService.checkStopPointExists(version.getParentServicePointSloid());
    locationService.allocateSloid(version, SloidType.PARKING_LOT);
    return parkingLotRepository.saveAndFlush(version);
  }

  public Page<ParkingLotVersion> findAll(ParkingLotSearchRestrictions searchRestrictions) {
    return parkingLotRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public List<ParkingLotVersion> findByParentServicePointSloid(String parentServicePointSloid) {
    return parkingLotRepository.findByParentServicePointSloid(parentServicePointSloid);
  }

  public Container<ReadParkingLotVersionModel> buildOverview(List<ParkingLotVersion> parkingLotVersions,
      Pageable pageable) {
    List<ParkingLotVersion> mergedVersions = OverviewService.mergeVersionsForDisplay(parkingLotVersions,
        (x, y) -> x.getSloid().equals(y.getSloid()));
    return OverviewService.toPagedContainer(mergedVersions.stream().map(ParkingLotVersionMapper::toModel).toList(),
        pageable);
  }
}
