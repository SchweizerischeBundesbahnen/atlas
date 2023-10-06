package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.InformationDeskTestData;
import ch.sbb.prm.directory.ParkingLotTestData;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPlaceTestData;
import ch.sbb.prm.directory.TicketCounterTestData;
import ch.sbb.prm.directory.ToiletTestData;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.InformationDeskRepository;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import ch.sbb.prm.directory.repository.TicketCounterRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class ReferencePointServiceTest {

  private final ReferencePointService referencePointService;
  private final PlatformRepository platformRepository;
  private final TicketCounterRepository ticketCounterRepository;
  private final ToiletRepository toiletRepository;
  private final InformationDeskRepository informationDeskRepository;
  private final ParkingLotRepository parkingLotRepository;

  private final RelationService relationService;

  private final StopPlaceRepository stopPlaceRepository;

  @Autowired
  ReferencePointServiceTest(ReferencePointService referencePointService, PlatformRepository platformRepository,
      TicketCounterRepository ticketCounterRepository, ToiletRepository toiletRepository,
      InformationDeskRepository informationDeskRepository, ParkingLotRepository parkingLotRepository, RelationService relationService,
      StopPlaceRepository stopPlaceRepository){
    this.referencePointService = referencePointService;
    this.platformRepository = platformRepository;
    this.ticketCounterRepository = ticketCounterRepository;
    this.toiletRepository = toiletRepository;
    this.informationDeskRepository = informationDeskRepository;
    this.parkingLotRepository = parkingLotRepository;
    this.relationService = relationService;
    this.stopPlaceRepository = stopPlaceRepository;
  }

  @Test
  void shouldCreateReferencePoint(){
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(parentServicePointSloid);
    stopPlaceRepository.save(stopPlaceVersion);
    createAndSavePlatformVersion(parentServicePointSloid);
    createAndSaveTicketCounterVersion(parentServicePointSloid);
    createAndSaveToiletVersion(parentServicePointSloid);
    createAndSaveInformationDeskVersion(parentServicePointSloid);
    createAndSaveParkingLotVersion(parentServicePointSloid);

    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);

    //when
    referencePointService.createReferencePoint(referencePointVersion);

    //then
    List<RelationVersion> relations = relationService.getRelationsByParentServicePoint(
        parentServicePointSloid);
    assertThat(relations).hasSize(5);
    assertThat(relations.stream().map(RelationVersion::getReferencePointElementType)).containsExactlyInAnyOrder(
        ReferencePointElementType.values());

  }

  private void createAndSaveParkingLotVersion(String parentServicePointSloid) {
    ParkingLotVersion parkingLot = ParkingLotTestData.getParkingLotVersion();
    parkingLot.setParentServicePointSloid(parentServicePointSloid);
    parkingLot.setSloid("ch:1:sloid:70000:5");
    parkingLotRepository.save(parkingLot);
  }
  private void createAndSaveInformationDeskVersion(String parentServicePointSloid) {
    InformationDeskVersion informationDesk = InformationDeskTestData.getInformationDeskVersion();
    informationDesk.setParentServicePointSloid(parentServicePointSloid);
    informationDesk.setSloid("ch:1:sloid:70000:4");
    informationDeskRepository.save(informationDesk);
  }
  private void createAndSaveToiletVersion(String parentServicePointSloid) {
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setParentServicePointSloid(parentServicePointSloid);
    toiletVersion.setSloid("ch:1:sloid:70000:3");
    toiletRepository.save(toiletVersion);
  }

  private void createAndSaveTicketCounterVersion(String parentServicePointSloid) {
    TicketCounterVersion ticketCounterversion = TicketCounterTestData.getTicketCounterVersion();
    ticketCounterversion.setParentServicePointSloid(parentServicePointSloid);
    ticketCounterversion.setSloid("ch:1:sloid:70000:2");
    ticketCounterRepository.save(ticketCounterversion);
  }

  private void createAndSavePlatformVersion(String parentServicePointSloid) {
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setParentServicePointSloid(parentServicePointSloid);
    platformVersion.setSloid("ch:1:sloid:70000:1");
    platformRepository.saveAndFlush(platformVersion);
  }

}