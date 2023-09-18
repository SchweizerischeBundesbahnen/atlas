package ch.sbb.line.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.line.directory.entity.TimetableFieldLineRelation;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
 class TimetableFieldNumberVersionRepositoryTest {

  private final TimetableFieldNumberVersionRepository versionRepository;
  private TimetableFieldNumberVersion version;

  @Autowired
   TimetableFieldNumberVersionRepositoryTest(
      TimetableFieldNumberVersionRepository versionRepository) {
    this.versionRepository = versionRepository;
  }

  @BeforeEach
  void setUpVersionWithTwoLineRelations() {
    version = TimetableFieldNumberVersion.builder()
                                         .ttfnid("ch:1:ttfnid:100000")
                                         .description("FPFN Description")
                                         .number("BEX")
                                         .status(Status.VALIDATED)
                                         .swissTimetableFieldNumber("b0.BEX")
                                         .validFrom(LocalDate.of(2020, 12, 12))
                                         .validTo(LocalDate.of(2020, 12, 12))
                                         .businessOrganisation("sbb")
                                         .build();
    version.setLineRelations(new HashSet<>(
        Set.of(TimetableFieldLineRelation.builder()
                                         .slnid("ch:1:slnid:100000")
                                         .timetableFieldNumberVersion(version)
                                         .build(),
            TimetableFieldLineRelation.builder()
                                      .slnid("ch:1:slnid:100001")
                                      .timetableFieldNumberVersion(version)
                                      .build())));
    version = versionRepository.save(version);

    assertThat(version.getCreator()).isEqualTo(WithMockJwtAuthentication.SBB_UID);
    assertThat(version.getEditor()).isEqualTo(WithMockJwtAuthentication.SBB_UID);
  }

  @Test
  void shouldGetSimpleVersion() {
    //given
    version.getLineRelations().clear();

    //when
    TimetableFieldNumberVersion result = versionRepository.findAll().get(0);

    //then
    assertThat(result).usingRecursiveComparison().ignoringActualNullFields().isEqualTo(version);
  }

  @Test
  void shouldGetCountVersions() {
    //when
    long result = versionRepository.count();

    //then
    assertThat(result).isEqualTo(1);
  }

  @Test
  void shouldGetVersionWithTwoLineRelations() {
    //given

    //when
    TimetableFieldNumberVersion result = versionRepository.findAll().get(0);

    //then
    assertThat(result).usingRecursiveComparison().ignoringActualNullFields().isEqualTo(version);
  }

  @Test
  void shouldUpdateVersionOnAllVersions() {
    //given
    assertThat(version.getVersion()).isZero();

    //when
    versionRepository.incrementVersion(version.getTtfnid());
    TimetableFieldNumberVersion result = versionRepository.findAll().get(0);

    //then
    assertThat(result.getVersion()).isEqualTo(1);
  }

  @Test
  void shouldUpdateVersionWithAdditionalLineRelation() {
    //given
    version.getLineRelations()
           .add(TimetableFieldLineRelation.builder()
                                          .slnid("ch:1:slnid:100002")
                                          .timetableFieldNumberVersion(version)
                                          .build());
    versionRepository.save(version);

    //when
    TimetableFieldNumberVersion result = versionRepository.findAll().get(0);

    //then
    assertThat(result.getLineRelations()).hasSize(3).extracting("id").isNotNull();
  }

  @Test
  void shouldUpdateVersionDeletingLineRelation() {
    //given
    version.getLineRelations().remove(version.getLineRelations().iterator().next());
    versionRepository.save(version);

    //when
    TimetableFieldNumberVersion result = versionRepository.findAll().get(0);

    //then
    assertThat(result.getLineRelations()).hasSize(1).extracting("id").isNotNull();
  }

  @Test
  void shouldDeleteVersion() {
    //given
    versionRepository.delete(version);

    //when
    List<TimetableFieldNumberVersion> result = versionRepository.findAll();

    //then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldDeleteVersions() {
    //given
    String ttfnid = "ch:1:ttfnid:100000";
    TimetableFieldNumberVersion secondVersion = TimetableFieldNumberVersion.builder()
                                                                           .ttfnid(
                                                                               "ch:1:ttfnid:100000")
                                                                           .description(
                                                                               "FPFN Description2")
                                                                           .number("BEX2")
                                                                           .status(Status.VALIDATED)
                                                                           .swissTimetableFieldNumber(
                                                                               "b0.BEX2")
                                                                           .validFrom(
                                                                               LocalDate.of(2021,
                                                                                   12, 12))
                                                                           .validTo(
                                                                               LocalDate.of(2021,
                                                                                   12, 12))
                                                                           .businessOrganisation(
                                                                               "sbb")
                                                                           .build();
    secondVersion.setLineRelations(new HashSet<>(
        Set.of(
            TimetableFieldLineRelation.builder()
                                      .slnid("ch:1:slnid:100000")
                                      .timetableFieldNumberVersion(secondVersion)
                                      .build(),
            TimetableFieldLineRelation.builder()
                                      .slnid("ch:1:slnid:100001")
                                      .timetableFieldNumberVersion(secondVersion)
                                      .build())));
    versionRepository.save(secondVersion);

    List<TimetableFieldNumberVersion> allVersionsVersioned = versionRepository.getAllVersionsVersioned(
        ttfnid);
    assertThat(allVersionsVersioned).hasSize(2);

    //when
    versionRepository.deleteAll(allVersionsVersioned);

    //then
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(ttfnid);
    assertThat(result).isEmpty();
  }
}
