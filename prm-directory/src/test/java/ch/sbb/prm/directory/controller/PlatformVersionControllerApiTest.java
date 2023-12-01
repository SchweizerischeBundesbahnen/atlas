package ch.sbb.prm.directory.controller;

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

import ch.sbb.atlas.api.prm.model.platform.CreatePlatformVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.service.RelationService;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class PlatformVersionControllerApiTest extends BaseControllerApiTest {

  private static final String PARENT_SERVICE_POINT_SLOID = "ch:1:sloid:7000";
  private final PlatformRepository platformRepository;
  private final StopPointRepository stopPointRepository;
  private final ReferencePointRepository referencePointRepository;
  private final SharedServicePointRepository sharedServicePointRepository;

  @MockBean
  private final RelationService relationService;

  @Autowired
  PlatformVersionControllerApiTest(PlatformRepository platformRepository,
                                   StopPointRepository stopPointRepository,
                                   ReferencePointRepository referencePointRepository,
                                   SharedServicePointRepository sharedServicePointRepository,
                                   RelationService relationService) {
    this.platformRepository = platformRepository;
    this.stopPointRepository = stopPointRepository;
    this.referencePointRepository = referencePointRepository;
    this.sharedServicePointRepository = sharedServicePointRepository;
    this.relationService = relationService;
  }

  @BeforeEach
  void setUp() {
    SharedServicePoint servicePoint = SharedServicePoint.builder()
            .servicePoint("{\"servicePointSloid\":\"ch:1:sloid:7000\",\"sboids\":[\"ch:1:sboid:100602\"],"
                    + "\"trafficPointSloids\":[\"ch:1:sloid:12345:1\"]}")
            .sloid("ch:1:sloid:7000")
            .build();
    sharedServicePointRepository.saveAndFlush(servicePoint);
  }

  @AfterEach
  void cleanUp() {
    sharedServicePointRepository.deleteAll();
  }

  @Test
  void shouldGetPlatformsVersion() throws Exception {
    //given
    platformRepository.save(PlatformTestData.getPlatformVersion());

    //when & then
    mvc.perform(get("/v1/platforms"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void shouldCreateCompletePlatform() throws Exception {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    CreatePlatformVersionModel createPlatformVersionModel = PlatformTestData.getCreateCompletePlatformVersionModel();
    createPlatformVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);

    //when && then
    mvc.perform(post("/v1/platforms")
            .contentType(contentType)
            .content(mapper.writeValueAsString(createPlatformVersionModel)))
        .andExpect(status().isCreated());
    verify(relationService, times(1)).save(any(RelationVersion.class));
  }

  @Test
  void shouldCreateReducedPlatform() throws Exception {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setMeansOfTransport(Set.of(MeanOfTransport.BUS));
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    CreatePlatformVersionModel createPlatformVersionModel = PlatformTestData.getCreateReducedPlatformVersionModel();
    createPlatformVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);

    //when && then
    mvc.perform(post("/v1/platforms")
            .contentType(contentType)
            .content(mapper.writeValueAsString(createPlatformVersionModel)))
        .andExpect(status().isCreated());
    verify(relationService, times(0)).save(any(RelationVersion.class));
  }

  @Test
  void shouldNotCreatePlatformReducedWhenCompletePropertiesProvided() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointVersion.setMeansOfTransport(Set.of(MeanOfTransport.BUS));
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);

    CreatePlatformVersionModel createPlatformVersionModel = PlatformTestData.getCreatePlatformVersionModel();
    createPlatformVersionModel.setParentServicePointSloid(parentServicePointSloid);

    //when && then
    mvc.perform(post("/v1/platforms")
            .contentType(contentType)
            .content(mapper.writeValueAsString(createPlatformVersionModel)))
        .andExpect(status().isBadRequest());
    verify(relationService, never()).save(any(RelationVersion.class));

  }

  @Test
  void shouldNotCreatePlatformCompletedWhenReducedPropertiesProvided() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointVersion.setMeansOfTransport(Set.of(MeanOfTransport.TRAIN));
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);

    CreatePlatformVersionModel createPlatformVersionModel = PlatformTestData.getCreateCompletePlatformVersionModel();
    createPlatformVersionModel.setParentServicePointSloid(parentServicePointSloid);
    createPlatformVersionModel.setHeight(123.1);

    //when && then
    mvc.perform(post("/v1/platforms")
            .contentType(contentType)
            .content(mapper.writeValueAsString(createPlatformVersionModel)))
        .andExpect(status().isBadRequest());
    verify(relationService, never()).save(any(RelationVersion.class));

  }

  @Test
  void shouldNotCreatePlatformWhenStopPointDoesNotExist() throws Exception {
    //given
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    CreatePlatformVersionModel createPlatformVersionModel = PlatformTestData.getCreatePlatformVersionModel();
    createPlatformVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);

    //when && then
    mvc.perform(post("/v1/platforms")
            .contentType(contentType)
            .content(mapper.writeValueAsString(createPlatformVersionModel)))
        .andExpect(status().isPreconditionFailed())
        .andExpect(jsonPath("$.message", is("The stop point with sloid ch:1:sloid:7000 does not exist.")));
    verify(relationService, times(0)).save(any(RelationVersion.class));
  }

  @Test
  void shouldNotCreatePlatformVersionWhenParentSloidDoesNotExist() throws Exception {
    //given
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid("ch:1:sloid:7001");
    referencePointRepository.save(referencePointVersion);

    CreatePlatformVersionModel createPlatformVersionModel = PlatformTestData.getCreatePlatformVersionModel();
    createPlatformVersionModel.setParentServicePointSloid("ch:1:sloid:7001");

    //when && then
    mvc.perform(post("/v1/platforms")
                    .contentType(contentType)
                    .content(mapper.writeValueAsString(createPlatformVersionModel)))
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
  void shouldUpdatePlatform() throws Exception {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.saveAndFlush(stopPointVersion);
    PlatformVersion version1 = PlatformTestData.builderCompleteVersion1().build();
    version1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    platformRepository.saveAndFlush(version1);
    PlatformVersion version2 = PlatformTestData.builderCompleteVersion2().build();
    version2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    platformRepository.saveAndFlush(version2);

    CreatePlatformVersionModel editedVersionModel = new CreatePlatformVersionModel();
    editedVersionModel.setSloid(version2.getSloid());
    editedVersionModel.setNumberWithoutCheckDigit(version2.getNumber().getNumber());
    editedVersionModel.setValidFrom(version2.getValidFrom());
    editedVersionModel.setValidTo(version2.getValidTo().minusYears(1));
    editedVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    editedVersionModel.setBoardingDevice(version2.getBoardingDevice());
    editedVersionModel.setAdditionalInformation(version2.getAdditionalInformation());
    editedVersionModel.setAdviceAccessInfo(version2.getAdviceAccessInfo());
    editedVersionModel.setContrastingAreas(version2.getContrastingAreas());
    editedVersionModel.setDynamicAudio(version2.getDynamicAudio());
    editedVersionModel.setDynamicVisual(version2.getDynamicVisual());
    editedVersionModel.setHeight(version2.getHeight());
    editedVersionModel.setInclination(version2.getInclination());
    editedVersionModel.setInclinationLongitudinal(version2.getInclinationLongitudinal());
    editedVersionModel.setInclinationWidth(version2.getInclinationWidth());
    editedVersionModel.setInfoOpportunities(null);
    editedVersionModel.setLevelAccessWheelchair(version2.getLevelAccessWheelchair());
    editedVersionModel.setPartialElevation(version2.getPartialElevation());
    editedVersionModel.setSuperelevation(version2.getSuperelevation());
    editedVersionModel.setTactileSystem(version2.getTactileSystem());
    editedVersionModel.setVehicleAccess(version2.getVehicleAccess());
    editedVersionModel.setWheelchairAreaLength(version2.getWheelchairAreaLength());
    editedVersionModel.setWheelchairAreaWidth(version2.getWheelchairAreaWidth());

    editedVersionModel.setCreationDate(version2.getCreationDate());
    editedVersionModel.setEditionDate(version2.getEditionDate());
    editedVersionModel.setCreator(version2.getCreator());
    editedVersionModel.setEditor(version2.getEditor());
    editedVersionModel.setEtagVersion(version2.getVersion());

    //when & then
    mvc.perform(put("/v1/platforms/" + version2.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(editedVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2000-01-01")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2000-12-31")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2001-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2001-12-31")));
  }

}
