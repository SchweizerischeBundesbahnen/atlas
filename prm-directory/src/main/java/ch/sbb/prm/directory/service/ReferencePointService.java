package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.INFORMATION_DESK;
import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.PARKING_LOT;
import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.PLATFORM;
import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.TICKET_COUNTER;
import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.TOILET;

import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.InformationDeskRepository;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.TicketCounterRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import ch.sbb.prm.directory.util.RelationUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ReferencePointService {

  private final ReferencePointRepository referencePointRepository;
  private final TicketCounterRepository ticketCounterService;
  private final ToiletRepository toiletRepository;
  private final InformationDeskRepository informationDeskRepository;
  private final ParkingLotRepository parkingLotRepository;
  private final PlatformRepository platformRepository;
  private final RelationService relationService;
  private final StopPlaceService stopPlaceService;

  public List<ReferencePointVersion> getAllReferencePoints() {
    return referencePointRepository.findAll();
  }

  public void createReferencePoint(ReferencePointVersion referencePointVersion) {
    stopPlaceService.checkStopPlaceExists(referencePointVersion.getParentServicePointSloid());
    searchAndUpdatePlatformRelation(referencePointVersion.getParentServicePointSloid());
    searchAndUpdateTicketCounter(referencePointVersion.getParentServicePointSloid());
    searchAndUpdateToiletRelation(referencePointVersion.getParentServicePointSloid());
    searchAndUpdateInformationDesk(referencePointVersion.getParentServicePointSloid());
    searchAndUpdateParkingLot(referencePointVersion.getParentServicePointSloid());
    referencePointRepository.save(referencePointVersion);
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
        version -> relationService.createRelation(RelationUtil.buildReleaseVersion(version, referencePointElementType)));
  }

}
