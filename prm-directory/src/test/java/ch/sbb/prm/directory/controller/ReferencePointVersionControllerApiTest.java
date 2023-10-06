package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.prm.directory.InformationDeskTestData;
import ch.sbb.prm.directory.ParkingLotTestData;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPlaceTestData;
import ch.sbb.prm.directory.TicketCounterTestData;
import ch.sbb.prm.directory.ToiletTestData;
import ch.sbb.prm.directory.controller.model.referencepoint.CreateReferencePointVersionModel;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.repository.InformationDeskRepository;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import ch.sbb.prm.directory.repository.TicketCounterRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import ch.sbb.prm.directory.service.RelationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class ReferencePointVersionControllerApiTest extends BaseControllerApiTest {

  private final ReferencePointRepository referencePointRepository;

  private final StopPlaceRepository stopPlaceRepository;

  private final InformationDeskRepository informationDeskRepository;
  private final TicketCounterRepository ticketCounterRepository;
  private final ParkingLotRepository parkingLotRepository;
  private final ToiletRepository toiletRepository;

  private final PlatformRepository platformRepository;
  @MockBean
  private final RelationService relationService;

  @Autowired
  ReferencePointVersionControllerApiTest(ReferencePointRepository referencePointRepository,
      StopPlaceRepository stopPlaceRepository, InformationDeskRepository informationDeskRepository,
      TicketCounterRepository ticketCounterRepository, ParkingLotRepository parkingLotRepository,
      ToiletRepository toiletRepository, PlatformRepository platformRepository, RelationService relationService){
    this.referencePointRepository = referencePointRepository;
    this.stopPlaceRepository = stopPlaceRepository;
    this.informationDeskRepository = informationDeskRepository;
    this.ticketCounterRepository = ticketCounterRepository;
    this.parkingLotRepository = parkingLotRepository;
    this.toiletRepository = toiletRepository;
    this.platformRepository = platformRepository;
    this.relationService = relationService;
  }

  @Test
  void shouldGetReferencePointsVersion() throws Exception {
    //given
    referencePointRepository.save(ReferencePointTestData.getReferencePointVersion());
    //when & then
    mvc.perform(get("/v1/reference-points"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void shouldCreateReferencePoint() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(parentServicePointSloid);
    stopPlaceRepository.save(stopPlaceVersion);
    CreateReferencePointVersionModel createReferencePointVersionModel = ReferencePointTestData.getCreateReferencePointVersionModel();
    createReferencePointVersionModel.setParentServicePointSloid(parentServicePointSloid);

    //Init PRM Relations
    InformationDeskVersion informationDesk = InformationDeskTestData.getInformationDeskVersion();
    informationDesk.setParentServicePointSloid(parentServicePointSloid);
    informationDeskRepository.save(informationDesk);
    TicketCounterVersion ticketCounterVersion = TicketCounterTestData.getTicketCounterVersion();
    ticketCounterVersion.setParentServicePointSloid(parentServicePointSloid);
    ticketCounterRepository.save(ticketCounterVersion);
    ParkingLotVersion parkingLotVersion = ParkingLotTestData.getParkingLotVersion();
    parkingLotVersion.setParentServicePointSloid(parentServicePointSloid);
    parkingLotRepository.save(parkingLotVersion);
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setParentServicePointSloid(parentServicePointSloid);
    toiletRepository.save(toiletVersion);
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setParentServicePointSloid(parentServicePointSloid);
    platformRepository.save(platformVersion);

    //when && then
    mvc.perform(post("/v1/reference-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(createReferencePointVersionModel)))
        .andExpect(status().isCreated());
    //verify that the reference point create 5 relation
    verify(relationService, times(5)).createRelation(any(RelationVersion.class));

  }

}
