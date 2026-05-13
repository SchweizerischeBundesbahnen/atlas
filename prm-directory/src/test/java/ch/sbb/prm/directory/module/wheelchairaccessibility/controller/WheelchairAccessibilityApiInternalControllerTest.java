package ch.sbb.prm.directory.module.wheelchairaccessibility.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.prm.directory.module.platform.PlatformTestData;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.platform.repository.PlatformRepository;
import ch.sbb.prm.directory.module.relation.RelationTestData;
import ch.sbb.prm.directory.module.relation.entity.RelationVersion;
import ch.sbb.prm.directory.module.relation.repository.RelationRepository;
import ch.sbb.prm.directory.module.stoppoint.StopPointTestData;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import ch.sbb.prm.directory.module.stoppoint.repository.StopPointRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class WheelchairAccessibilityApiInternalControllerTest extends BaseControllerApiTest {

  private final StopPointRepository stopPointRepository;
  private final PlatformRepository platformRepository;
  private final RelationRepository relationRepository;

  @Autowired
  WheelchairAccessibilityApiInternalControllerTest(StopPointRepository stopPointRepository,
      PlatformRepository platformRepository,
      RelationRepository relationRepository) {
    this.stopPointRepository = stopPointRepository;
    this.platformRepository = platformRepository;
    this.relationRepository = relationRepository;
  }

  @AfterEach
  void cleanUp() {
    stopPointRepository.deleteAll();
    platformRepository.deleteAll();
    relationRepository.deleteAll();
  }

  @Test
  void shouldGetWheelchairAccessibilityValidTodayPlatform() throws Exception {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setValidFrom(LocalDate.now());
    stopPointVersion.setValidTo(LocalDate.now().plusYears(1));

    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setValidFrom(LocalDate.now());
    platformVersion.setValidTo(LocalDate.now().plusYears(1));

    stopPointRepository.save(stopPointVersion);
    platformRepository.save(platformVersion);
    relationRepository.save(RelationTestData.getRelation(stopPointVersion.getSloid(),
        platformVersion.getSloid(),
        ReferencePointElementType.PLATFORM));

    //when & then
    mvc.perform(get("/internal/wheelchair-accessibility/" + platformVersion.getSloid() + "/platform"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.state").value(WheelchairAccessibilityState.NO_ACCESS.name()));
  }

  @Test
  void shouldGetWheelchairAccessibilityValidTodayStopPoint() throws Exception {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setValidFrom(LocalDate.now());
    stopPointVersion.setValidTo(LocalDate.now().plusYears(1));

    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setValidFrom(LocalDate.now());
    platformVersion.setValidTo(LocalDate.now().plusYears(1));
    platformVersion.setShuttle(BooleanOptionalAttributeType.YES);

    RelationVersion relationVersion = RelationTestData.getRelation(stopPointVersion.getSloid(),
        platformVersion.getSloid(),
        ReferencePointElementType.PLATFORM);
    relationVersion.setStepFreeAccess(StepFreeAccessAttributeType.YES);

    stopPointRepository.save(stopPointVersion);
    platformRepository.save(platformVersion);
    relationRepository.save(relationVersion);

    //when & then
    mvc.perform(get("/internal/wheelchair-accessibility/" + stopPointVersion.getSloid() + "/stop-point"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.state").value(WheelchairAccessibilityState.SHUTTLE.name()));
  }

}
