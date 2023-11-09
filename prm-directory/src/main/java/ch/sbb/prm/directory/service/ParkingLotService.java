package ch.sbb.prm.directory.service;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.SharedServicePointVersionModel;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PARKING_LOT;

@Service
@Transactional
public class ParkingLotService extends PrmRelatableVersionableService<ParkingLotVersion> {

  private final ParkingLotRepository parkingLotRepository;

  public ParkingLotService(ParkingLotRepository parkingLotRepository, StopPointService stopPointService,
      RelationService relationService, ReferencePointRepository referencePointRepository, VersionableService versionableService) {
    super(versionableService, stopPointService, relationService, referencePointRepository);
    this.parkingLotRepository = parkingLotRepository;
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
    versionableService.applyVersioning(ParkingLotVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(parkingLotRepository));
  }

  public List<ParkingLotVersion> getAllParkingLots() {
    return parkingLotRepository.findAll();
  }

  @PreAuthorize("""
      @prmBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisations
      (#sharedServicePointVersionModel, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).PRM)""")
  public ParkingLotVersion createParkingLot(ParkingLotVersion version,
                                            SharedServicePointVersionModel sharedServicePointVersionModel) {
    createRelation(version);
    return save(version);
  }

  @PreAuthorize("""
      @prmBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisations
      (#sharedServicePointVersionModel, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).PRM)""")
  public ParkingLotVersion updateParkingLotVersion(ParkingLotVersion currentVersion, ParkingLotVersion editedVersion,
                                                   SharedServicePointVersionModel sharedServicePointVersionModel) {
    return updateVersion(currentVersion, editedVersion);
  }

  public List<ParkingLotVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return parkingLotRepository.findAllByNumberOrderByValidFrom(number);
  }

  public Optional<ParkingLotVersion> getPlatformVersionById(Long id) {
    return parkingLotRepository.findById(id);
  }
}
