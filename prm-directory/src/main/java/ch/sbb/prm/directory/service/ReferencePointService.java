package ch.sbb.prm.directory.service;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.INFORMATION_DESK;
import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PARKING_LOT;
import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PLATFORM;
import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.TICKET_COUNTER;
import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.TOILET;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.repository.InformationDeskRepository;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.TicketCounterRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import ch.sbb.prm.directory.util.RelationUtil;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReferencePointService extends PrmVersionableService<ReferencePointVersion> {

  private final ReferencePointRepository referencePointRepository;
  private final TicketCounterRepository ticketCounterService;
  private final ToiletRepository toiletRepository;
  private final InformationDeskRepository informationDeskRepository;
  private final ParkingLotRepository parkingLotRepository;
  private final PlatformRepository platformRepository;
  private final RelationService relationService;
  private final StopPointService stopPointService;

  public ReferencePointService(ReferencePointRepository referencePointRepository, TicketCounterRepository ticketCounterService,
      ToiletRepository toiletRepository, InformationDeskRepository informationDeskRepository,
      ParkingLotRepository parkingLotRepository, PlatformRepository platformRepository, RelationService relationService,
      StopPointService stopPointService, VersionableService versionableService) {
    super(versionableService);
    this.referencePointRepository = referencePointRepository;
    this.ticketCounterService = ticketCounterService;
    this.toiletRepository = toiletRepository;
    this.informationDeskRepository = informationDeskRepository;
    this.parkingLotRepository = parkingLotRepository;
    this.platformRepository = platformRepository;
    this.relationService = relationService;
    this.stopPointService = stopPointService;
  }

  @Override
  protected void incrementVersion(ServicePointNumber servicePointNumber) {
    referencePointRepository.incrementVersion(servicePointNumber);
  }

  @Override
  protected ReferencePointVersion save(ReferencePointVersion version) {
    return this.saveReferencePoint(version);
  }

  @Override
  protected List<ReferencePointVersion> getAllVersions(ServicePointNumber servicePointNumber) {
    return this.findAllByNumberOrderByValidFrom(servicePointNumber);
  }

  @Override
  protected void applyVersioning(List<VersionedObject> versionedObjects) {
    versionableService.applyVersioning(ReferencePointVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(referencePointRepository));
  }

  public List<ReferencePointVersion> getAllReferencePoints() {
    return referencePointRepository.findAll();
  }

  public ReferencePointVersion createReferencePoint(ReferencePointVersion referencePointVersion) {
    stopPointService.checkStopPointExists(referencePointVersion.getParentServicePointSloid());
    searchAndUpdatePlatformRelation(referencePointVersion.getParentServicePointSloid());
    searchAndUpdateTicketCounter(referencePointVersion.getParentServicePointSloid());
    searchAndUpdateToiletRelation(referencePointVersion.getParentServicePointSloid());
    searchAndUpdateInformationDesk(referencePointVersion.getParentServicePointSloid());
    searchAndUpdateParkingLot(referencePointVersion.getParentServicePointSloid());
    return referencePointRepository.saveAndFlush(referencePointVersion);
  }

  public ReferencePointVersion updateReferencePointVersion(ReferencePointVersion currentVersion,
      ReferencePointVersion editedVersion) {
    return updateVersion(currentVersion, editedVersion);
  }

  public Optional<ReferencePointVersion> getReferencePointById(Long id) {
    return referencePointRepository.findById(id);
  }

  public List<ReferencePointVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return referencePointRepository.findAllByNumberOrderByValidFrom(number);
  }

  private ReferencePointVersion saveReferencePoint(ReferencePointVersion referencePointVersion) {
    return referencePointRepository.saveAndFlush(referencePointVersion);
  }

  private void searchAndUpdateParkingLot(String parentServicePointSloid) {
    List<ParkingLotVersion> parkingLotVersions = parkingLotRepository.findByParentServicePointSloid(
        parentServicePointSloid);
    searchAndUpdateVersion(parkingLotVersions, PARKING_LOT);
  }

  private void searchAndUpdateInformationDesk(String parentServicePointSloid) {
    List<InformationDeskVersion> informationDeskVersions = informationDeskRepository.findByParentServicePointSloid(
        parentServicePointSloid);
    searchAndUpdateVersion(informationDeskVersions, INFORMATION_DESK);
  }

  private void searchAndUpdateTicketCounter(String parentServicePointSloid) {
    List<TicketCounterVersion> ticketCounterVersions = ticketCounterService.findByParentServicePointSloid(
        parentServicePointSloid);
    searchAndUpdateVersion(ticketCounterVersions, TICKET_COUNTER);
  }

  private void searchAndUpdatePlatformRelation(String parentServicePointSloid) {
    List<PlatformVersion> platformVersions = platformRepository.findByParentServicePointSloid(parentServicePointSloid);
    searchAndUpdateVersion(platformVersions, PLATFORM);
  }

  private void searchAndUpdateToiletRelation(String parentServicePointSloid) {
    List<ToiletVersion> toiletVersions = toiletRepository.findByParentServicePointSloid(parentServicePointSloid);
    searchAndUpdateVersion(toiletVersions, TOILET);
  }

  private void searchAndUpdateVersion(List<? extends Relatable> versions,
      ReferencePointElementType referencePointElementType) {
    versions.forEach(
        version -> relationService.save(RelationUtil.buildRelationVersion(version, referencePointElementType)));
  }

}
