package ch.sbb.prm.directory.service;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PLATFORM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.kafka.model.service.point.SharedServicePointVersionModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPlaceTestData;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.exception.StopPlaceDoesNotExistsException;
import ch.sbb.prm.directory.exception.TrafficPointElementDoesNotExistsException;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class PlatformServiceTest {

  private static final String PARENT_SERVICE_POINT_SLOID = "ch:1:sloid:70000";

  private final PlatformRepository platformRepository;
  private final ReferencePointRepository referencePointRepository;
  private final RelationRepository relationRepository;

  private final StopPlaceRepository stopPlaceRepository;
  private final PlatformService platformService;
  private final SharedServicePointConsumer sharedServicePointConsumer;

  @Autowired
  PlatformServiceTest(PlatformRepository platformRepository, ReferencePointRepository referencePointRepository,
      RelationRepository relationRepository, StopPlaceRepository stopPlaceRepository, PlatformService platformService,
      SharedServicePointConsumer sharedServicePointConsumer) {
    this.platformRepository = platformRepository;
    this.referencePointRepository = referencePointRepository;
    this.relationRepository = relationRepository;
    this.stopPlaceRepository = stopPlaceRepository;
    this.platformService = platformService;
    this.sharedServicePointConsumer = sharedServicePointConsumer;
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
  void shouldNotCreatePlatformWhenStopPlaceDoesNotExists() {
    //given
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);

    //when & then
    assertThrows(StopPlaceDoesNotExistsException.class,
        () -> platformService.createPlatformVersion(platformVersion)).getLocalizedMessage();
  }

  @Test
  void shouldCreatePlatformWhenNoReferencePointExists() {
    //given
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPlaceRepository.save(stopPlaceVersion);
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when
    platformService.createPlatformVersion(platformVersion);

    //then
    List<PlatformVersion> platformVersions = platformRepository.findByParentServicePointSloid(
        platformVersion.getParentServicePointSloid());
    assertThat(platformVersions).hasSize(1);
    assertThat(platformVersions.get(0).getParentServicePointSloid()).isEqualTo(platformVersion.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(
        platformVersion.getParentServicePointSloid());
    assertThat(relationVersions).isEmpty();
  }

  @Test
  void shouldCreatePlatformWhenReferencePointExists() {
    //given
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPlaceRepository.save(stopPlaceVersion);
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
    assertThat(platformVersions.get(0).getParentServicePointSloid()).isEqualTo(platformVersion.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(
        PARENT_SERVICE_POINT_SLOID);
    assertThat(relationVersions).hasSize(1);
    assertThat(relationVersions.get(0).getParentServicePointSloid()).isEqualTo(PARENT_SERVICE_POINT_SLOID);
    assertThat(relationVersions.get(0).getReferencePointElementType()).isEqualTo(PLATFORM);
  }

  @Test
  void shouldNotCreatePlatformWhenTrafficPointDoesNotExistOnSePoDi() {
    //given
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    platformVersion.setSloid("unknown sloid");

    //when & then
    assertThrows(TrafficPointElementDoesNotExistsException.class,
        () -> platformService.createPlatformVersion(platformVersion)).getLocalizedMessage();
  }

}