package ch.sbb.timetable.field.number.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.IntegrationTest;
import ch.sbb.timetable.field.number.WithMockJwtAuthentication;
import ch.sbb.timetable.field.number.entity.LineRelation;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.enumaration.Status;
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
public class VersionRepositoryTest {

  private final VersionRepository versionRepository;
  private Version version;

  @Autowired
  public VersionRepositoryTest(VersionRepository versionRepository) {
    this.versionRepository = versionRepository;
  }

  @BeforeEach
  void setUpVersionWithTwoLineRelations() {
    version = Version.builder()
        .ttfnid("ch:1:ttfnid:100000")
        .description("FPFN Description")
        .number("BEX")
        .status(Status.ACTIVE)
        .swissTimetableFieldNumber("b0.BEX")
        .validFrom(LocalDate.of(2020, 12, 12))
        .validTo(LocalDate.of(2020, 12, 12))
        .businessOrganisation("sbb")
        .build();
    version.setLineRelations(new HashSet<>(
        Set.of(LineRelation.builder().slnid("ch:1:slnid:100000").version(version).build(),
            LineRelation.builder().slnid("ch:1:slnid:100001").version(version).build())));
    version = versionRepository.save(version);

    assertThat(version.getCreator()).isEqualTo(WithMockJwtAuthentication.SBB_UID);
    assertThat(version.getEditor()).isEqualTo(WithMockJwtAuthentication.SBB_UID);
  }

  @Test
  void shouldGetSimpleVersion() {
    //given
    version.getLineRelations().clear();

    //when
    Version result = versionRepository.findAll().get(0);

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
    Version result = versionRepository.findAll().get(0);

    //then
    assertThat(result).usingRecursiveComparison().ignoringActualNullFields().isEqualTo(version);
  }

  @Test
  void shouldUpdateVersionWithAdditionalLineRelation() {
    //given
    version.getLineRelations()
        .add(LineRelation.builder().slnid("ch:1:slnid:100002").version(version).build());
    versionRepository.save(version);

    //when
    Version result = versionRepository.findAll().get(0);

    //then
    assertThat(result.getLineRelations()).hasSize(3).extracting("id").isNotNull();
  }

  @Test
  void shouldUpdateVersionDeletingLineRelation() {
    //given
    version.getLineRelations().remove(version.getLineRelations().iterator().next());
    versionRepository.save(version);

    //when
    Version result = versionRepository.findAll().get(0);

    //then
    assertThat(result.getLineRelations()).hasSize(1).extracting("id").isNotNull();
  }

  @Test
  void shouldDeleteVersion() {
    //given
    versionRepository.delete(version);

    //when
    List<Version> result = versionRepository.findAll();

    //then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldDeleteVersions() {
    //given
    String ttfnid = "ch:1:ttfnid:100000";
    Version secondVersion = Version.builder()
                     .ttfnid("ch:1:ttfnid:100000")
                     .description("FPFN Description2")
                     .number("BEX2")
                     .status(Status.ACTIVE)
                     .swissTimetableFieldNumber("b0.BEX2")
                     .validFrom(LocalDate.of(2021, 12, 12))
                     .validTo(LocalDate.of(2021, 12, 12))
                     .businessOrganisation("sbb")
                     .build();
    secondVersion.setLineRelations(new HashSet<>(
        Set.of(LineRelation.builder().slnid("ch:1:slnid:100000").version(secondVersion).build(),
            LineRelation.builder().slnid("ch:1:slnid:100001").version(secondVersion).build())));
    versionRepository.save(secondVersion);

    List<Version> allVersionsVersioned = versionRepository.getAllVersionsVersioned(ttfnid);
    assertThat(allVersionsVersioned.size()).isEqualTo(2);

    //when
    versionRepository.deleteAll(allVersionsVersioned);

    //then
    List<Version> result = versionRepository.getAllVersionsVersioned(ttfnid);
    assertThat(result).isEmpty();
  }
}
