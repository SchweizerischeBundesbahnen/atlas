package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.prm.model.parkinglot.CreateParkingLotVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.ParkingLotTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.service.RelationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class ParkingLotVersionControllerApiTest extends BaseControllerApiTest {

  private static final String PARENT_SERVICE_POINT_SLOID = "ch:1:sloid:7000";
  private final ParkingLotRepository parkingLotRepository;
  private final StopPointRepository stopPointRepository;
  private final ReferencePointRepository referencePointRepository;
  private final SharedServicePointRepository sharedServicePointRepository;

  @MockBean
  private final RelationService relationService;

  @Autowired
  ParkingLotVersionControllerApiTest(ParkingLotRepository parkingLotRepository,
                                     StopPointRepository stopPointRepository,
                                     ReferencePointRepository referencePointRepository,
                                     SharedServicePointRepository sharedServicePointRepository,
                                     RelationService relationService) {
    this.parkingLotRepository = parkingLotRepository;
    this.stopPointRepository = stopPointRepository;
    this.referencePointRepository = referencePointRepository;
    this.sharedServicePointRepository = sharedServicePointRepository;
    this.relationService = relationService;
  }

  @BeforeEach
  void setUp() {
    SharedServicePoint servicePoint = SharedServicePoint.builder()
            .servicePoint("{\"servicePointSloid\":\"ch:1:sloid:7000\",\"sboids\":[\"ch:1:sboid:100602\"],"
                    + "\"trafficPointSloids\":[]}")
            .sloid("ch:1:sloid:7000")
            .build();
    sharedServicePointRepository.saveAndFlush(servicePoint);
  }

  @AfterEach
  void cleanUp() {
    sharedServicePointRepository.deleteAll();
  }

  @Test
  void shouldGetParkingLotVersion() throws Exception {
    //given
    parkingLotRepository.save(ParkingLotTestData.getParkingLotVersion());
    //when & then
    mvc.perform(get("/v1/parking-lots"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void shouldCreateParkingLot() throws Exception {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    CreateParkingLotVersionModel model = ParkingLotTestData.getCreateParkingLotVersionModel();
    model.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);

    //when && then
    mvc.perform(post("/v1/parking-lots")
            .contentType(contentType)
            .content(mapper.writeValueAsString(model)))
        .andExpect(status().isCreated());
    verify(relationService, times(1)).save(any(RelationVersion.class));
  }

  @Test
  void shouldCreateParkingLotWithReducedStopPoint() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointVersion.setMeansOfTransport(Set.of(MeanOfTransport.BUS));
    stopPointRepository.save(stopPointVersion);

    CreateParkingLotVersionModel model = ParkingLotTestData.getCreateParkingLotVersionModel();
    model.setParentServicePointSloid(parentServicePointSloid);

    //when && then
    mvc.perform(post("/v1/parking-lots")
            .contentType(contentType)
            .content(mapper.writeValueAsString(model)))
        .andExpect(status().isCreated());
    verify(relationService, never()).save(any(RelationVersion.class));

  }

  @Test
  void shouldNotCreateParkingLotWhenStopPointDoesNotExist() throws Exception {
    //given
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    CreateParkingLotVersionModel model = ParkingLotTestData.getCreateParkingLotVersionModel();
    model.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);

    //when && then
    mvc.perform(post("/v1/parking-lots")
            .contentType(contentType)
            .content(mapper.writeValueAsString(model)))
        .andExpect(status().isPreconditionFailed())
        .andExpect(jsonPath("$.message", is("The stop point with sloid ch:1:sloid:7000 does not exist.")));
    verify(relationService, times(0)).save(any(RelationVersion.class));
  }

  @Test
  void shouldNotCreateParkingLotVersionWhenParentSloidDoesNotExist() throws Exception {
    //given
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid("ch:1:sloid:7001");
    referencePointRepository.save(referencePointVersion);

    CreateParkingLotVersionModel model = ParkingLotTestData.getCreateParkingLotVersionModel();
    model.setParentServicePointSloid("ch:1:sloid:7001");

    //when && then
    mvc.perform(post("/v1/parking-lots")
                    .contentType(contentType)
                    .content(mapper.writeValueAsString(model)))
            .andExpect(status().isPreconditionFailed())
            .andExpect(jsonPath("$.message", is("The service point with sloid ch:1:sloid:7001 does not exist.")));
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
  void shouldUpdateParkingLot() throws Exception {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);
    ParkingLotVersion version1 = ParkingLotTestData.builderVersion1().build();
    version1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    parkingLotRepository.saveAndFlush(version1);
    ParkingLotVersion version2 = ParkingLotTestData.builderVersion2().build();
    version2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    parkingLotRepository.saveAndFlush(version2);

    CreateParkingLotVersionModel editedVersionModel = new CreateParkingLotVersionModel();
    editedVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    editedVersionModel.setSloid(version2.getSloid());
    editedVersionModel.setValidFrom(version2.getValidFrom());
    editedVersionModel.setValidTo(version2.getValidTo().minusYears(1));
    editedVersionModel.setNumberWithoutCheckDigit(version2.getNumber().getNumber());
    editedVersionModel.setDesignation(version2.getDesignation());
    editedVersionModel.setAdditionalInformation(version2.getAdditionalInformation());
    editedVersionModel.setPlacesAvailable(version2.getPlacesAvailable());
    editedVersionModel.setPrmPlacesAvailable(version2.getPrmPlacesAvailable());
    editedVersionModel.setCreationDate(version2.getCreationDate());
    editedVersionModel.setEditionDate(version2.getEditionDate());
    editedVersionModel.setCreator(version2.getCreator());
    editedVersionModel.setEditor(version2.getEditor());
    editedVersionModel.setEtagVersion(version2.getVersion());

    //when & then
    mvc.perform(put("/v1/parking-lots/" + version2.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(editedVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2000-01-01")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2000-12-31")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2001-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2001-12-31")));
  }

}
