package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPlaceTestData;
import ch.sbb.prm.directory.controller.model.platform.CreatePlatformVersionModel;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import ch.sbb.prm.directory.service.RelationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class PlatformVersionControllerApiTest extends BaseControllerApiTest {

  private final PlatformRepository platformRepository;

  private final StopPlaceRepository stopPlaceRepository;
  private final ReferencePointRepository referencePointRepository;

  @MockBean
  private final RelationService relationService;

  @Autowired
  PlatformVersionControllerApiTest(PlatformRepository platformRepository, StopPlaceRepository stopPlaceRepository,
      ReferencePointRepository referencePointRepository, RelationService relationService) {
    this.platformRepository = platformRepository;
    this.stopPlaceRepository = stopPlaceRepository;
    this.referencePointRepository = referencePointRepository;
    this.relationService = relationService;
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
  void shouldCreatePlatform() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(parentServicePointSloid);
    stopPlaceRepository.save(stopPlaceVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);

    CreatePlatformVersionModel createPlatformVersionModel = PlatformTestData.getCreatePlatformVersionModel();
    createPlatformVersionModel.setParentServicePointSloid(parentServicePointSloid);

    //when && then
    mvc.perform(post("/v1/platforms")
            .contentType(contentType)
            .content(mapper.writeValueAsString(createPlatformVersionModel)))
        .andExpect(status().isCreated());
    verify(relationService, times(1)).save(any(RelationVersion.class));

  }

  @Test
  void shouldNotCreatePlatformWhenStopPlaceDoesNotExists() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);

    CreatePlatformVersionModel createPlatformVersionModel = PlatformTestData.getCreatePlatformVersionModel();
    createPlatformVersionModel.setParentServicePointSloid(parentServicePointSloid);

    //when && then
    mvc.perform(post("/v1/platforms")
            .contentType(contentType)
            .content(mapper.writeValueAsString(createPlatformVersionModel)))
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
  void shouldUpdatePlatform() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(parentServicePointSloid);
    stopPlaceRepository.saveAndFlush(stopPlaceVersion);
    PlatformVersion version1 = PlatformTestData.builderVersion1().build();
    version1.setParentServicePointSloid(parentServicePointSloid);
    platformRepository.saveAndFlush(version1);
    PlatformVersion version2 = PlatformTestData.builderVersion2().build();
    version2.setParentServicePointSloid(parentServicePointSloid);
    platformRepository.saveAndFlush(version2);

    CreatePlatformVersionModel editedVersionModel = new CreatePlatformVersionModel();
    editedVersionModel.setSloid(version2.getSloid());
    editedVersionModel.setNumberWithoutCheckDigit(version2.getNumber().getNumber());
    editedVersionModel.setValidFrom(version2.getValidFrom());
    editedVersionModel.setValidTo(version2.getValidTo().minusYears(1));
    editedVersionModel.setParentServicePointSloid(parentServicePointSloid);
    editedVersionModel.setBoardingDevice(version2.getBoardingDevice());
    editedVersionModel.setAdditionalInfo(version2.getAdditionalInfo());
    editedVersionModel.setAdviceAccessInfo(version2.getAdviceAccessInfo());
    editedVersionModel.setContrastingAreas(version2.getContrastingAreas());
    editedVersionModel.setDynamicAudio(version2.getDynamicAudio());
    editedVersionModel.setDynamicVisual(version2.getDynamicVisual());
    editedVersionModel.setHeight(version2.getHeight());
    editedVersionModel.setInclination(version2.getInclination());
    editedVersionModel.setInclinationLongitudinal(version2.getInclinationLongitudinal());
    editedVersionModel.setInclinationWidth(version2.getInclinationWidth());
    editedVersionModel.setInfoOpportunities(version2.getInfoOpportunities().stream().toList());
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
