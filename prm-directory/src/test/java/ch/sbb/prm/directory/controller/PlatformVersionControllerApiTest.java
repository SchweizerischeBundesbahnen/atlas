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

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.model.platform.PlatformVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.SharedServicePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.BasePrmImportEntity.Fields;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.service.PrmLocationService;
import ch.sbb.prm.directory.service.RelationService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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

  @MockBean
  private final PrmLocationService prmLocationService;

  @Autowired
  PlatformVersionControllerApiTest(PlatformRepository platformRepository,
      StopPointRepository stopPointRepository,
      ReferencePointRepository referencePointRepository,
      SharedServicePointRepository sharedServicePointRepository,
      RelationService relationService, PrmLocationService prmLocationService) {
    this.platformRepository = platformRepository;
    this.stopPointRepository = stopPointRepository;
    this.referencePointRepository = referencePointRepository;
    this.sharedServicePointRepository = sharedServicePointRepository;
    this.relationService = relationService;
    this.prmLocationService = prmLocationService;
  }

  @BeforeEach
  void setUp() {
    SharedServicePoint servicePoint = SharedServicePointTestData.buildSharedServicePoint("ch:1:sloid:7000", Set.of("ch:1:sboid:100602"),
        Set.of("ch:1:sloid:12345:1"));
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
        .andExpect(jsonPath("$.objects", hasSize(1)))
        .andExpect(jsonPath("$.objects[0]." + Fields.status, is(Status.VALIDATED.name())));
  }

  @Test
  void shouldGetPlatformsVersionByParentSloid() throws Exception {
    //given
    platformRepository.save(PlatformTestData.getPlatformVersion());

    //when & then
    mvc.perform(
            get("/v1/platforms?parentServicePointSloids=" + PlatformTestData.getPlatformVersion().getParentServicePointSloid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(1)));
  }

  @Test
  void shouldGetPlatformBySloid() throws Exception {
    //given
    platformRepository.save(PlatformTestData.getPlatformVersion());

    //when & then
    mvc.perform(get("/v1/platforms/" + PlatformTestData.getPlatformVersion().getSloid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void shouldGetPlatformVersionsWithFilter() throws Exception {
    //given
    PlatformVersion version = platformRepository.save(PlatformTestData.getPlatformVersion());

    //when & then
    mvc.perform(get("/v1/platforms" +
            "?numbers=12345" +
            "&sloids=ch:1:sloid:12345:1" +
            "&fromDate=" + version.getValidFrom() +
            "&statusRestrictions=VALIDATED" +
            "&toDate=" + version.getValidTo() +
            "&validOn=" + LocalDate.of(2000, 6, 28) +
            "&createdAfter=" + version.getCreationDate().minusSeconds(1)
            .format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)) +
            "&modifiedAfter=" + version.getEditionDate()
            .format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN))
        ))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(1)));
  }

  @Test
  void shouldGetPlatformOverview() throws Exception {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    platformRepository.save(platformVersion);

    //when & then
    mvc.perform(get("/v1/platforms/overview/" + platformVersion.getParentServicePointSloid()))
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

    PlatformVersionModel platformVersionModel = PlatformTestData.getCreateCompletePlatformVersionModel();
    platformVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);

    //when && then
    mvc.perform(post("/v1/platforms")
            .contentType(contentType)
            .content(mapper.writeValueAsString(platformVersionModel)))
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

    PlatformVersionModel platformVersionModel = PlatformTestData.getCreateReducedPlatformVersionModel();
    platformVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);

    //when && then
    mvc.perform(post("/v1/platforms")
            .contentType(contentType)
            .content(mapper.writeValueAsString(platformVersionModel)))
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

    PlatformVersionModel platformVersionModel = PlatformTestData.getPlatformVersionModel();
    platformVersionModel.setParentServicePointSloid(parentServicePointSloid);

    //when && then
    mvc.perform(post("/v1/platforms")
            .contentType(contentType)
            .content(mapper.writeValueAsString(platformVersionModel)))
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

    PlatformVersionModel platformVersionModel = PlatformTestData.getCreateCompletePlatformVersionModel();
    platformVersionModel.setParentServicePointSloid(parentServicePointSloid);
    platformVersionModel.setHeight(123.1);

    //when && then
    mvc.perform(post("/v1/platforms")
            .contentType(contentType)
            .content(mapper.writeValueAsString(platformVersionModel)))
        .andExpect(status().isBadRequest());
    verify(relationService, never()).save(any(RelationVersion.class));

  }

  @Test
  void shouldNotCreatePlatformWhenStopPointDoesNotExist() throws Exception {
    //given
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    PlatformVersionModel platformVersionModel = PlatformTestData.getPlatformVersionModel();
    platformVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);

    //when && then
    mvc.perform(post("/v1/platforms")
            .contentType(contentType)
            .content(mapper.writeValueAsString(platformVersionModel)))
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

    PlatformVersionModel platformVersionModel = PlatformTestData.getPlatformVersionModel();
    platformVersionModel.setParentServicePointSloid("ch:1:sloid:7001");

    //when && then
    mvc.perform(post("/v1/platforms")
            .contentType(contentType)
            .content(mapper.writeValueAsString(platformVersionModel)))
        .andExpect(status().isPreconditionFailed())
        .andExpect(jsonPath("$.message", is("The service point with sloid ch:1:sloid:7001 does not exist.")));
  }

  /**
   * Szenario 8a: Letzte Version terminieren wenn nur validTo ist updated NEU:      |______________________| IST:
   * |------------------------------------------------------- Version:                            1
   * <p>
   * RESULTAT: |----------------------| Version wird per xx aufgehoben Version:         1
   */
  @Test
  void shouldUpdatePlatform() throws Exception {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.saveAndFlush(stopPointVersion);
    PlatformVersion version1 = PlatformTestData.builderCompleteVersion1().build();
    version1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    version1.setInfoOpportunities(Collections.emptySet());
    platformRepository.saveAndFlush(version1);
    PlatformVersion version2 = PlatformTestData.builderCompleteVersion2().build();
    version2.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    version2.setInfoOpportunities(Collections.emptySet());
    version2 = platformRepository.saveAndFlush(version2);

    PlatformVersionModel editedVersionModel = new PlatformVersionModel();
    editedVersionModel.setSloid(version2.getSloid());
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

  /**
   * Szenario ATLAS-1885
   * Increasing validFrom should move version
   */
  @Test
  void shouldUpdatePlatformMovingValidFromToFuture() throws Exception {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.saveAndFlush(stopPointVersion);

    PlatformVersion version1 = PlatformTestData.builderCompleteVersion1().build();
    version1.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    version1.setInfoOpportunities(Collections.emptySet());
    platformRepository.saveAndFlush(version1);

    PlatformVersionModel editedVersionModel = new PlatformVersionModel();
    editedVersionModel.setSloid(version1.getSloid());
    editedVersionModel.setValidFrom(version1.getValidFrom().plusMonths(1));
    editedVersionModel.setValidTo(version1.getValidTo());
    editedVersionModel.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    editedVersionModel.setBoardingDevice(version1.getBoardingDevice());
    editedVersionModel.setAdditionalInformation(version1.getAdditionalInformation());
    editedVersionModel.setAdviceAccessInfo(version1.getAdviceAccessInfo());
    editedVersionModel.setContrastingAreas(version1.getContrastingAreas());
    editedVersionModel.setDynamicAudio(version1.getDynamicAudio());
    editedVersionModel.setDynamicVisual(version1.getDynamicVisual());
    editedVersionModel.setHeight(version1.getHeight());
    editedVersionModel.setInclination(version1.getInclination());
    editedVersionModel.setInclinationLongitudinal(version1.getInclinationLongitudinal());
    editedVersionModel.setInclinationWidth(version1.getInclinationWidth());
    editedVersionModel.setLevelAccessWheelchair(version1.getLevelAccessWheelchair());
    editedVersionModel.setPartialElevation(version1.getPartialElevation());
    editedVersionModel.setSuperelevation(version1.getSuperelevation());
    editedVersionModel.setTactileSystem(version1.getTactileSystem());
    editedVersionModel.setVehicleAccess(version1.getVehicleAccess());
    editedVersionModel.setWheelchairAreaLength(version1.getWheelchairAreaLength());
    editedVersionModel.setWheelchairAreaWidth(version1.getWheelchairAreaWidth());

    editedVersionModel.setCreationDate(version1.getCreationDate());
    editedVersionModel.setEditionDate(version1.getEditionDate());
    editedVersionModel.setCreator(version1.getCreator());
    editedVersionModel.setEditor(version1.getEditor());
    editedVersionModel.setEtagVersion(version1.getVersion());

    //when & then
    mvc.perform(put("/v1/platforms/" + version1.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(editedVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2000-02-01")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2000-12-31")));
  }

}
