package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.service.point.SharedServicePointVersionModel;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PlatformRelationServiceIntegrationTest extends BasePrmServiceTest {

  private static final String SERVICE_POINT_SLOID = "ch:1:sloid:7000";
  private static final String TRAFFIC_POINT_SLOID = "ch:1:sloid:7000:0:1";
  private static final String REFERENCE_POINT_SLOID = "ch:1:7000:1";

  private final StopPointService stopPointService;
  private final ReferencePointService referencePointService;
  private final PlatformService platformService;
  private final RelationService relationService;
  private final SharedServicePointConsumer sharedServicePointConsumer;

  @Autowired
  private PlatformRelationServiceIntegrationTest(
      StopPointService stopPointService,
      ReferencePointService referencePointService,
      PlatformService platformService,
      RelationService relationService,
      SharedServicePointConsumer sharedServicePointConsumer,
      SharedServicePointRepository sharedServicePointRepository,
      PrmLocationService prmLocationService) {
    super(sharedServicePointRepository, prmLocationService);
    this.stopPointService = stopPointService;
    this.referencePointService = referencePointService;
    this.platformService = platformService;
    this.relationService = relationService;
    this.sharedServicePointConsumer = sharedServicePointConsumer;
  }

  @Override
  @BeforeEach
  void setUp() {
    sharedServicePointConsumer.readServicePointFromKafka(SharedServicePointVersionModel.builder()
        .servicePointSloid(SERVICE_POINT_SLOID)
        .sboids(Set.of("ch:1:sboid:100001"))
        .trafficPointSloids(Set.of(TRAFFIC_POINT_SLOID))
        .stopPoint(true)
        .build());
  }

  @Test
  void shouldCreateStopPointWithOneReferencePointButTwoVersionsAndThenCreatePlatformSuccessfully() {
    // given complete stopPoint
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(SERVICE_POINT_SLOID);
    assertThat(stopPointVersion.isReduced()).isFalse();
    stopPointService.save(stopPointVersion);

    // with 2 ReferencePoint Versions
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(SERVICE_POINT_SLOID);
    referencePointVersion.setSloid(REFERENCE_POINT_SLOID);
    ReferencePointVersion referencePointVersion1 = referencePointService.createReferencePoint(referencePointVersion);

    ReferencePointVersion referencePointVersion2 = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion2.setParentServicePointSloid(SERVICE_POINT_SLOID);
    referencePointVersion2.setSloid(REFERENCE_POINT_SLOID);
    referencePointVersion2.setDesignation("edited designation");
    referencePointVersion2.setValidFrom(LocalDate.of(2000, 3, 1));
    referencePointVersion2.setVersion(referencePointVersion1.getVersion());
    referencePointService.updateReferencePointVersion(referencePointVersion1, referencePointVersion2);

    assertThat(referencePointService.findByParentServicePointSloid(SERVICE_POINT_SLOID)).hasSize(2);

    // and be able to create platform
    PlatformVersion platformVersion = PlatformTestData.getCompletePlatformVersion();
    platformVersion.setParentServicePointSloid(SERVICE_POINT_SLOID);
    platformVersion.setSloid(TRAFFIC_POINT_SLOID);

    platformVersion = platformService.createPlatformVersion(platformVersion);

    // Platform create was successful
    assertThat(platformVersion).isNotNull();

    // One Relation was created
    List<RelationVersion> relations = relationService.getRelationsBySloid(platformVersion.getSloid());
    assertThat(relations).hasSize(1);

    // Validity check
    RelationVersion onlyRelation = relations.getFirst();
    assertThat(onlyRelation.getValidFrom()).isEqualTo(platformVersion.getValidFrom());
    assertThat(onlyRelation.getValidTo()).isEqualTo(platformVersion.getValidTo());
  }

}
