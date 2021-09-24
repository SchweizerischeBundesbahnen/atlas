package ch.sbb.line.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.IntegrationTest;
import ch.sbb.line.directory.entity.LineRelation;
import ch.sbb.line.directory.entity.Version;
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
    version = Version.builder().ttfnid("ch:1:fpfnid:100000")
                     .name("FPFN Name")
                     .number("BEX")
                     .swissTimetableFieldNumber("b0.BEX")
                     .validFrom(LocalDate.of(2020, 12, 12))
                     .validTo(LocalDate.of(2099, 12, 12))
                     .build();
    version.setLineRelations(new HashSet<>(
        Set.of(LineRelation.builder().slnid("ch:1:slnid:100000").version(version).build(),
            LineRelation.builder().slnid("ch:1:slnid:100001").version(version).build())));
    version = versionRepository.save(version);
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
}