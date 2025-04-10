package ch.sbb.prm.directory.service;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PLATFORM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.api.prm.model.platform.PlatformOverviewModel;
import ch.sbb.atlas.kafka.model.service.point.SharedServicePointVersionModel;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.controller.model.PrmObjectRequestParams;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.ElementTypeDoesNotExistException;
import ch.sbb.prm.directory.exception.PlatformAlreadyExistsException;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistException;
import ch.sbb.prm.directory.exception.TrafficPointElementDoesNotExistsException;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.search.PlatformSearchRestrictions;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

class PlatformServiceTest extends BasePrmServiceTest {

  private static final String PLATFORM_SLOID = PARENT_SERVICE_POINT_SLOID + ":1";

  private final PlatformService platformService;
  private final PlatformRepository platformRepository;
  private final SharedServicePointConsumer sharedServicePointConsumer;
  private final RelationRepository relationRepository;
  private final StopPointRepository stopPointRepository;
  private final ReferencePointRepository referencePointRepository;

  @Autowired
  PlatformServiceTest(PlatformService platformService,
      PlatformRepository platformRepository,
      SharedServicePointConsumer sharedServicePointConsumer,
      RelationRepository relationRepository,
      StopPointRepository stopPointRepository,
      ReferencePointRepository referencePointRepository,
      SharedServicePointRepository sharedServicePointRepository,
      PrmLocationService prmLocationService) {
    super(sharedServicePointRepository, prmLocationService);
    this.platformService = platformService;
    this.platformRepository = platformRepository;
    this.sharedServicePointConsumer = sharedServicePointConsumer;
    this.relationRepository = relationRepository;
    this.stopPointRepository = stopPointRepository;
    this.referencePointRepository = referencePointRepository;
  }

  @Override
  @BeforeEach
  void setUp() {
    sharedServicePointConsumer.readServicePointFromKafka(SharedServicePointVersionModel.builder()
        .servicePointSloid(PARENT_SERVICE_POINT_SLOID)
        .sboids(Set.of("ch:1:sboid:100001"))
        .trafficPointSloids(Set.of("ch:1:sloid:12345:1", PLATFORM_SLOID))
        .stopPoint(true)
        .build());
  }

  @AfterEach
  void tearDown() {
    platformRepository.deleteAll();
  }

  @Test
  void shouldNotCreatePlatformWhenStopPointDoesNotExist() {
    //given
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when & then
    assertThrows(StopPointDoesNotExistException.class,
        () -> platformService.createPlatformVersion(platformVersion)).getLocalizedMessage();
  }

  @Test
  void shouldCreatePlatformWhenNoReferencePointExists() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    PlatformVersion platformVersion = PlatformTestData.getCompletePlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when
    platformService.createPlatformVersion(platformVersion);

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

    PlatformVersion platformVersion = PlatformTestData.getCompletePlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);

    //when
    platformService.createPlatformVersion(platformVersion);

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
    StopPointVersion stopPointVersion = StopPointTestData.builderVersion1().meansOfTransport(Set.of(MeanOfTransport.TRAIN))
        .build();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    PlatformVersion platformVersion = PlatformTestData.getCompletePlatformVersion();
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
        () -> platformService.createPlatformVersion(platformVersion)).getLocalizedMessage();
  }

  @Test
  void shouldFindPlatformsByParentSloid() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.builderVersion1().meansOfTransport(Set.of(MeanOfTransport.TRAIN))
        .build();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    PlatformVersion platformVersion = PlatformTestData.getCompletePlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    platformService.createPlatformVersion(platformVersion);

    //when
    List<PlatformVersion> platformVersions = platformService.findAll(PlatformSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .prmObjectRequestParams(
                PrmObjectRequestParams.builder().parentServicePointSloids(List.of(PARENT_SERVICE_POINT_SLOID)).build()).build())
        .getContent();

    //then
    assertThat(platformVersions).hasSize(1);
  }

  @Test
  void shouldReturnPlatformsByStopPoint() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.builderVersion1().meansOfTransport(Set.of(MeanOfTransport.TRAIN))
        .build();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    PlatformVersion platformVersion = PlatformTestData.getCompletePlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    platformService.createPlatformVersion(platformVersion);

    //when
    List<PlatformVersion> platformVersions = platformService.getPlatformsByStopPoint(PARENT_SERVICE_POINT_SLOID);

    //then
    assertThat(platformVersions).hasSize(1);
  }

  @Test
  void shouldReturnPlatformOverviewMerged() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.builderVersion1().meansOfTransport(Set.of(MeanOfTransport.TRAIN))
        .build();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);

    PlatformVersion platformVersion = PlatformTestData.getCompletePlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    platformService.createPlatformVersion(platformVersion);

    //when
    List<PlatformOverviewModel> platformVersions = platformService.mergePlatformsForOverview(
        platformService.getPlatformsByStopPoint(PARENT_SERVICE_POINT_SLOID), PARENT_SERVICE_POINT_SLOID);

    //then
    assertThat(platformVersions).hasSize(1);
  }

  @Test
  void testCheckPlatformExists_Exists() {
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setSloid("ch:1:sloid:12345:1");
    platformRepository.saveAndFlush(platformVersion);

    assertDoesNotThrow(() -> platformService.checkPlatformExists("ch:1:sloid:12345:1", PLATFORM.name()));
  }

  @Test
  void testCheckPlatformExists_DoesNotExist() {
    assertThrows(ElementTypeDoesNotExistException.class,
        () -> platformService.checkPlatformExists("ch:1:sloid:12345:1", PLATFORM.name()));
  }

  @Test
  void shouldNotCreatePlatformWhenSloidAlreadyExists() {
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setSloid("ch:1:sloid:12345:1");
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    platformRepository.saveAndFlush(platformVersion);

    assertThrows(PlatformAlreadyExistsException.class, () -> platformService.createPlatformVersion(platformVersion));
  }

  @Test
  void shouldUpdatePlatformWithOnlyValidFromUpdate() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);

    PlatformVersion platformVersion = PlatformTestData.getCompletePlatformVersion();
    platformVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    platformVersion.setSloid(PLATFORM_SLOID);
    PlatformVersion currentVersion = platformService.createPlatformVersion(platformVersion);

    PlatformVersion editedVersion = PlatformTestData.getCompletePlatformVersion();
    editedVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    editedVersion.setSloid(PLATFORM_SLOID);
    editedVersion.setValidFrom(LocalDate.of(2000, 4, 1));
    editedVersion.setVersion(currentVersion.getVersion());

    platformService.updatePlatformVersion(currentVersion, editedVersion);

    List<PlatformVersion> platform = platformService.getAllVersions(platformVersion.getSloid());
    assertThat(platform).hasSize(1);
  }
}
