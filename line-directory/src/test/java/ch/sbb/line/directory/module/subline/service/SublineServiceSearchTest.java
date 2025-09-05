package ch.sbb.line.directory.module.subline.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.SublineVersionRequestParams;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.module.subline.SublineTestData;
import ch.sbb.line.directory.module.subline.entity.SublineVersion;
import ch.sbb.line.directory.module.subline.repository.SublineVersionRepository;
import ch.sbb.line.directory.module.subline.search.SublineVersionSearchRestrictions;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class SublineServiceSearchTest {

  private final SublineVersionRepository sublineVersionRepository;
  private final SublineService sublineService;

  @Autowired
  SublineServiceSearchTest(SublineVersionRepository sublineVersionRepository, SublineService sublineService) {
    this.sublineVersionRepository = sublineVersionRepository;
    this.sublineService = sublineService;
  }

  @BeforeEach
  void init() {
    String sublineSlnid = "ch:1:slnid:123:1";
    SublineVersion sublineVersion = SublineTestData.sublineVersionBuilder().slnid(sublineSlnid).build();
    sublineVersionRepository.save(sublineVersion);
  }

  @AfterEach
  void cleanUp() {
    sublineVersionRepository.deleteAll();
  }

  @Test
  void shouldFindSublineVersionsByCreatedAfter() {
    Page<SublineVersion> sublineVersions = sublineService.findAllVersions(SublineVersionSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .sublineVersionRequestParams(SublineVersionRequestParams.builder().createdAfter(LocalDateTime.now().minusDays(1)).build())
        .build());
    assertThat(sublineVersions.getTotalElements()).isEqualTo(1);
  }

  @Test
  void shouldFindSublineVersionsByModifiedAfter() {
    Page<SublineVersion> sublineVersions = sublineService.findAllVersions(SublineVersionSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .sublineVersionRequestParams(
            SublineVersionRequestParams.builder().modifiedAfter(LocalDateTime.now().minusDays(1)).build())
        .build());
    assertThat(sublineVersions.getTotalElements()).isEqualTo(1);
  }

  @Test
  void shouldFindSublineVersionsByMainlineSlnid() {
    Page<SublineVersion> sublineVersions = sublineService.findAllVersions(SublineVersionSearchRestrictions.builder()
        .pageable(Pageable.unpaged())
        .sublineVersionRequestParams(SublineVersionRequestParams.builder().mainlineSlnid(SublineTestData.MAINLINE_SLNID).build())
        .build());
    assertThat(sublineVersions.getTotalElements()).isEqualTo(1);
  }

}