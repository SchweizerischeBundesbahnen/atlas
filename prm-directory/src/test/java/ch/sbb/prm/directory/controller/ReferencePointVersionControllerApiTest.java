package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
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
import ch.sbb.prm.directory.entity.ReferencePointVersion;
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
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
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
    verify(relationService, times(5)).save(any(RelationVersion.class));

  }

  @Test
  void shouldNotCreateReferencePointWhenStopPlaceDoesNotExists() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
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
        .andExpect(status().isPreconditionFailed())
        .andExpect(jsonPath("$.message", is("The stop place with sloid ch:1:sloid:7000 does not exists.")));
    verify(relationService, times(0)).save(any(RelationVersion.class));
  }

  /**
   * Szenario 8a: Letzte Version terminieren wenn nur validTo ist updated
   * NEU:      |______________________|
   * IST:      |-------------------------------------------------------
   * Version:                            1
   *
   * RESULTAT: |----------------------| Version wird per xx aufgehoben
   * Version:         1
   */
  @Test
  void shouldUpdateReferencePoint() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(parentServicePointSloid);
    stopPlaceRepository.save(stopPlaceVersion);

    ReferencePointVersion version1 = ReferencePointTestData.builderVersion1().build();
    version1.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.saveAndFlush(version1);

    ReferencePointVersion version2 = ReferencePointTestData.builderVersion2().build();
    version2.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.saveAndFlush(version2);

    CreateReferencePointVersionModel editedVersionModel = new CreateReferencePointVersionModel();
    editedVersionModel.setParentServicePointSloid(parentServicePointSloid);
    editedVersionModel.setNumberWithoutCheckDigit(1234567);
    editedVersionModel.setMainReferencePoint(version2.isMainReferencePoint());
    editedVersionModel.setReferencePointType(version2.getReferencePointType());
    editedVersionModel.setDesignation(version2.getDesignation());
    editedVersionModel.setValidFrom(version2.getValidFrom());
    editedVersionModel.setValidTo(LocalDate.of(2001, 12, 31));
    editedVersionModel.setCreationDate(version2.getCreationDate());
    editedVersionModel.setEditionDate(version2.getEditionDate());
    editedVersionModel.setCreator(version2.getCreator());
    editedVersionModel.setEditor(version2.getEditor());
    editedVersionModel.setEtagVersion(version2.getVersion());

    //when & then
    mvc.perform(post("/v1/reference-points/" + version2.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(editedVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2000-01-01")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2000-12-31")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2001-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2001-12-31")));

  }


}
