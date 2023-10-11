package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.PARKING_LOT;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ParkingLotService extends PrmRelatableVersionableService<ParkingLotVersion> {

  private final ParkingLotRepository parkingLotRepository;

  private final VersionableService versionableService;

  public ParkingLotService(ParkingLotRepository parkingLotRepository, StopPlaceService stopPlaceService,
      RelationService relationService, ReferencePointRepository referencePointRepository, VersionableService versionableService) {
    super(versionableService,stopPlaceService,relationService,referencePointRepository);
    this.parkingLotRepository = parkingLotRepository;
    this.versionableService = versionableService;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return PARKING_LOT;
  }

  @Override
  protected void incrementVersion(ServicePointNumber servicePointNumber) {
    parkingLotRepository.incrementVersion(servicePointNumber);
  }

  @Override
  protected ParkingLotVersion save(ParkingLotVersion version) {
    return parkingLotRepository.saveAndFlush(version);
  }

  @Override
  protected List<ParkingLotVersion> getAllVersions(ServicePointNumber servicePointNumber) {
    return this.findAllByNumberOrderByValidFrom(servicePointNumber);
  }

  @Override
  protected void applyVersioning(List<VersionedObject> versionedObjects) {
    versionableService.applyVersioning(ParkingLotVersion.class, versionedObjects,this::save,
        new ApplyVersioningDeleteByIdLongConsumer(parkingLotRepository));
  }
  public List<ParkingLotVersion> getAllParkingLots() {
   return parkingLotRepository.findAll();
  }

  public ParkingLotVersion createParkingLot(ParkingLotVersion version) {
    createRelation(version);
    return save(version);
  }

  public ParkingLotVersion updateParkingLotVersion(ParkingLotVersion currentVersion, ParkingLotVersion editedVersion){
    return updateVersion(currentVersion,editedVersion);
  }

  public List<ParkingLotVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return parkingLotRepository.findAllByNumberOrderByValidFrom(number);
  }

  public Optional<ParkingLotVersion> getPlatformVersionById(Long id) {
    return parkingLotRepository.findById(id);
  }
}
