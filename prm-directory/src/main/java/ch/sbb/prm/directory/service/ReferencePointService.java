package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.INFORMATION_DESK;
import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.PARKING_LOT;
import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.PLATFORM;
import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.TICKET_COUNTER;
import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.TOILET;

import ch.sbb.prm.directory.entity.BasePrmEntityVersion;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.enumeration.StandardAttributeType;
import ch.sbb.prm.directory.enumeration.StepFreeAccessAttributeType;
import ch.sbb.prm.directory.enumeration.TactileVisualAttributeType;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ReferencePointService {

  private final ReferencePointRepository referencePointRepository;
  private final RelationService relationService;
  private final TicketCounterService ticketCounterService;
  private final ToiletService toiletService;
  private final InformationDeskService informationDeskService;
  private final ParkingLotService parkingLotService;
  private final PlatformService platformService;

  public List<ReferencePointVersion> getAllReferencePoints() {
    return referencePointRepository.findAll();
  }

  public List<ReferencePointVersion> getAllReferencesByServicePointParentSloid(String parentServicePointSloid) {
    return referencePointRepository.findByParentServicePointSloid(parentServicePointSloid);
  }

  public void createReferencePoint(ReferencePointVersion referencePointVersion) {
    searchAndUpdatePlatformRelation(referencePointVersion.getParentServicePointSloid());
    searchAndUpdateTicketCounter(referencePointVersion.getParentServicePointSloid());
    searchAndUpdateToiletRelation(referencePointVersion.getParentServicePointSloid());
    searchAndUpdateInformationDesk(referencePointVersion.getParentServicePointSloid());
    searchAndUpdateParkingLot(referencePointVersion.getParentServicePointSloid());
    referencePointRepository.save(referencePointVersion);
  }

  private void searchAndUpdateParkingLot(String parentServicePointSloid) {
    List<ParkingLotVersion> parkingLotVersions = parkingLotService.getByServicePointParentSloid(
        parentServicePointSloid);
    parkingLotVersions.forEach(version -> relationService.createRelation(buildReleaseVersion(version, PARKING_LOT)));
  }

  private void searchAndUpdateInformationDesk(String parentServicePointSloid) {
    List<InformationDeskVersion> informationDeskVersions = informationDeskService.getByServicePointParentSloid(
        parentServicePointSloid);
    informationDeskVersions.forEach(version -> relationService.createRelation(buildReleaseVersion(version, INFORMATION_DESK)));
  }

  void searchAndUpdateTicketCounter(String parentServicePointSloid) {
    List<TicketCounterVersion> ticketCounterVersions = ticketCounterService.getByServicePointParentSloid(parentServicePointSloid);
    ticketCounterVersions.forEach(version -> relationService.createRelation(buildReleaseVersion(version, TICKET_COUNTER)));
  }

  void searchAndUpdatePlatformRelation(String parentServicePointSloid) {
    List<PlatformVersion> platformVersions = platformService.getByServicePointParentSloid(parentServicePointSloid);
    platformVersions.forEach(version -> relationService.createRelation(buildReleaseVersion(version, PLATFORM)));
  }

  void searchAndUpdateToiletRelation(String parentServicePointSloid) {
    List<ToiletVersion> toiletVersions = toiletService.getByServicePointParentSloid(parentServicePointSloid);
    toiletVersions.forEach(version -> relationService.createRelation(buildReleaseVersion(version, TOILET)));
  }

  private RelationVersion buildReleaseVersion(BasePrmEntityVersion version, ReferencePointElementType referencePointElementType) {
    return RelationVersion.builder()
        .sloid(version.getSloid())
        .number(version.getNumber())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .parentServicePointSloid(version.getParentServicePointSloid())
        .referencePointElementType(referencePointElementType)
        .contrastingAreas(StandardAttributeType.TO_BE_COMPLETED)
        .tactileVisualMarks(TactileVisualAttributeType.TO_BE_COMPLETED)
        .stepFreeAccess(StepFreeAccessAttributeType.TO_BE_COMPLETED)
        .build();
  }

}
