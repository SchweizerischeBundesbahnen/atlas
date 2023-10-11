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
import org.hibernate.StaleObjectStateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ParkingLotService extends RelatableService<ParkingLotVersion> {

  private final ParkingLotRepository parkingLotRepository;

  private final VersionableService versionableService;

  public ParkingLotService(ParkingLotRepository parkingLotRepository, StopPlaceService stopPlaceService,
      RelationService relationService, ReferencePointRepository referencePointRepository, VersionableService versionableService) {
    super(stopPlaceService,relationService,referencePointRepository);
    this.parkingLotRepository = parkingLotRepository;
    this.versionableService = versionableService;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return PARKING_LOT;
  }

  public List<ParkingLotVersion> getAllParkingLots() {
   return parkingLotRepository.findAll();
  }

  public ParkingLotVersion createParkingLot(ParkingLotVersion version) {
    createRelation(version);
    return save(version);
  }

  private ParkingLotVersion save(ParkingLotVersion version) {
    return parkingLotRepository.saveAndFlush(version);
  }

  public ParkingLotVersion updateParkingLotVersion(ParkingLotVersion currentVersion, ParkingLotVersion editedVersion){
    checkStaleObjectIntegrity(currentVersion, editedVersion);
    editedVersion.setSloid(currentVersion.getSloid());
    editedVersion.setNumber(currentVersion.getNumber());
    List<ParkingLotVersion> existingDbVersions = parkingLotRepository.findAllByNumberOrderByValidFrom(
        currentVersion.getNumber());
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion, existingDbVersions);
    versionableService.applyVersioning(ParkingLotVersion.class, versionedObjects,
        this::save, new ApplyVersioningDeleteByIdLongConsumer(parkingLotRepository));
    return currentVersion;
  }

  private void checkStaleObjectIntegrity(ParkingLotVersion currentVersion, ParkingLotVersion editedVersion) {
    parkingLotRepository.incrementVersion(currentVersion.getNumber());
    if (editedVersion.getVersion() != null && !currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(ParkingLotVersion.class.getSimpleName(), "version");
    }
  }

  public List<ParkingLotVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return parkingLotRepository.findAllByNumberOrderByValidFrom(number);
  }

  public Optional<ParkingLotVersion> getPlatformVersionById(Long id) {
    return parkingLotRepository.findById(id);
  }
}
