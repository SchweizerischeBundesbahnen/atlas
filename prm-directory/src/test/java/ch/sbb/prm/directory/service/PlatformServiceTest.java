package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.PLATFORM;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class PlatformServiceTest {

  private final PlatformRepository platformRepository;
  private final ReferencePointRepository referencePointRepository;
  private final RelationRepository relationRepository;
  private final PlatformService platformService;

  @Autowired
  PlatformServiceTest(PlatformRepository platformRepository, ReferencePointRepository referencePointRepository,
      RelationRepository relationRepository, PlatformService platformService) {
    this.platformRepository = platformRepository;
    this.referencePointRepository = referencePointRepository;
    this.relationRepository = relationRepository;
    this.platformService = platformService;
  }

  @Test
  void shouldCreatePlatformWhenNoReferencePointExists() {
    //given
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();

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
    String parentServicePointSloid = "ch:1:sloid:70000";
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);

    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    platformVersion.setParentServicePointSloid(parentServicePointSloid);

    //when
    platformService.createPlatformVersion(platformVersion);

    //then
    List<PlatformVersion> platformVersions = platformRepository.findByParentServicePointSloid(
        platformVersion.getParentServicePointSloid());
    assertThat(platformVersions).hasSize(1);
    assertThat(platformVersions.get(0).getParentServicePointSloid()).isEqualTo(platformVersion.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(
        parentServicePointSloid);
    assertThat(relationVersions).hasSize(1);
    assertThat(relationVersions.get(0).getParentServicePointSloid()).isEqualTo(parentServicePointSloid);
    assertThat(relationVersions.get(0).getReferencePointElementType()).isEqualTo(PLATFORM);
  }

}