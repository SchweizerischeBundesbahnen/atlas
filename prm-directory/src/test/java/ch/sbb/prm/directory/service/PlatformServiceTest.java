package ch.sbb.prm.directory.service;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.SharedServicePointVersionModel;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistsException;
import ch.sbb.prm.directory.exception.TrafficPointElementDoesNotExistsException;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PLATFORM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@IntegrationTest
@Transactional
class PlatformServiceTest {

  private static final String PARENT_SERVICE_POINT_SLOID = "ch:1:sloid:70000";
  private static final SharedServicePointVersionModel SHARED_SERVICE_POINT_VERSION_MODEL =
          new SharedServicePointVersionModel(PARENT_SERVICE_POINT_SLOID,
                  Collections.singleton("sboid"),
                  Collections.singleton(""));

  private final PlatformService platformService;
  private final SharedServicePointConsumer sharedServicePointConsumer;
  private final PlatformRepository platformRepository;
  private final RelationRepository relationRepository;
  private final StopPointRepository stopPointRepository;
  private final ReferencePointRepository referencePointRepository;

  @Autowired
  PlatformServiceTest(PlatformService platformService, SharedServicePointConsumer sharedServicePointConsumer,
                      PlatformRepository platformRepository, RelationRepository relationRepository,
                      StopPointRepository stopPointRepository, ReferencePointRepository referencePointRepository) {
    this.platformService = platformService;
    this.sharedServicePointConsumer = sharedServicePointConsumer;
    this.platformRepository = platformRepository;
    this.relationRepository = relationRepository;
    this.stopPointRepository = stopPointRepository;
    this.referencePointRepository = referencePointRepository;
  }

  @BeforeEach
  void setUp() {
    sharedServicePointConsumer.readServicePointFromKafka(SharedServicePointVersionModel.builder()
        .servicePointSloid(PARENT_SERVICE_POINT_SLOID)
        .sboids(Set.of("ch:1:sboid:100001"))
        .trafficPointSloids(Set.of("ch:1:sloid:12345:1"))
        .build());
  }

  @Test
  void shouldNotCreatePlatformWhenStopPointDoesNotExist() {
    //given
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when & then
    assertThrows(StopPointDoesNotExistsException.class,
        () -> platformService.createPlatformVersion(platformVersion, SHARED_SERVICE_POINT_VERSION_MODEL)).getLocalizedMessage();
  }

  @Test
  void shouldCreatePlatformWhenNoReferencePointExists() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when
    platformService.createPlatformVersion(platformVersion, SHARED_SERVICE_POINT_VERSION_MODEL);

    //then
    List<PlatformVersion> platformVersions = platformRepository
            .findByParentServicePointSloid(platformVersion.getParentServicePointSloid());
    assertThat(platformVersions).hasSize(1);
    assertThat(platformVersions.get(0).getParentServicePointSloid()).isEqualTo(platformVersion.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository
            .findAllByParentServicePointSloid(platformVersion.getParentServicePointSloid());
    assertThat(relationVersions).isEmpty();
  }

  @Test
  void shouldCreatePlatformWhenReferencePointExists() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);

    //when
    platformService.createPlatformVersion(platformVersion, SHARED_SERVICE_POINT_VERSION_MODEL);

    //then
    List<PlatformVersion> platformVersions = platformRepository.findByParentServicePointSloid(
        platformVersion.getParentServicePointSloid());
    assertThat(platformVersions).hasSize(1);
    assertThat(platformVersions.get(0).getParentServicePointSloid()).isEqualTo(platformVersion.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository
            .findAllByParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    assertThat(relationVersions).hasSize(1);
    assertThat(relationVersions.get(0).getParentServicePointSloid()).isEqualTo(PARENT_SERVICE_POINT_SLOID);
    assertThat(relationVersions.get(0).getReferencePointElementType()).isEqualTo(PLATFORM);
  }

  /**
   * When Stop Point is reduced no one Relation must be added even if referencePoint exists
   */
  @Test
  void shouldCreatePlatformWhenStopPointIsComplete() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.builderVersion1().meansOfTransport(Set.of(MeanOfTransport.TRAIN)).build();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);

    //when
    platformService.createPlatformVersion(platformVersion);

    //then
    List<PlatformVersion> platformVersions = platformRepository.findByParentServicePointSloid(
        platformVersion.getParentServicePointSloid());
    assertThat(platformVersions).hasSize(1);
    PlatformVersion platformVersionResult = platformVersions.get(0);
    assertThat(platformVersionResult).isEqualTo(platformVersion);
    assertThat(platformVersionResult.getParentServicePointSloid()).isEqualTo(platformVersion.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(
        PARENT_SERVICE_POINT_SLOID);
    assertThat(relationVersions).hasSize(1);
    assertThat(relationVersions.get(0).getParentServicePointSloid()).isEqualTo(PARENT_SERVICE_POINT_SLOID);
    assertThat(relationVersions.get(0).getReferencePointElementType()).isEqualTo(PLATFORM);
  }

  /**
   * When Stop Point is reduced no one Relation must be added even if referencePoint exists
   */
  @Test
  void shouldCreatePlatformWhenStopPointIsReduced() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.builderVersion1().meansOfTransport(Set.of(MeanOfTransport.BUS)).build();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    PlatformVersion platformVersion = PlatformTestData.getReducedPlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);

    //when
    platformService.createPlatformVersion(platformVersion);

    //then
    List<PlatformVersion> platformVersions = platformRepository.findByParentServicePointSloid(
        platformVersion.getParentServicePointSloid());
    assertThat(platformVersions).hasSize(1);
    PlatformVersion platformVersionResult = platformVersions.get(0);
    assertThat(platformVersionResult).isEqualTo(platformVersion);
    assertThat(platformVersionResult.getParentServicePointSloid()).isEqualTo(platformVersion.getParentServicePointSloid());
    //when Stop Point is reduced no one Relation must be added even if referencePoint exists
    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(
        PARENT_SERVICE_POINT_SLOID);
    assertThat(relationVersions).isEmpty();
  }

  @Test
  void shouldNotCreatePlatformWhenTrafficPointDoesNotExistOnSePoDi() {
    //given
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    platformVersion.setSloid("unknown sloid");

    //when & then
    assertThrows(TrafficPointElementDoesNotExistsException.class,
        () -> platformService.createPlatformVersion(platformVersion, SHARED_SERVICE_POINT_VERSION_MODEL)).getLocalizedMessage();
  }

}