package ch.sbb.line.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.IntegrationTest;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.entity.Version;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import java.awt.Color;
import java.time.LocalDate;
import java.util.Collections;
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
                     .sublineVersions(Collections.emptySet())
                     .status(Status.ACTIVE)
                     .type(LineType.ORDERLY)
                     .slnid("slnid")
                     .paymentType(PaymentType.INTERNATIONAL)
                     .shortName("shortName")
                     .alternativeName("alternativeName")
                     .combinationName("combinationName")
                     .longName("longName")
                     .colorFontRgb(Color.black)
                     .colorBackRgb(Color.black)
                     .colorFontCmyk(Color.black)
                     .colorBackCmyk(Color.black)
                     .description("description")
                     .validFrom(LocalDate.of(2020, 12, 12))
                     .validTo(LocalDate.of(2099, 12, 12))
                     .businessOrganisation("businessOrganisation")
                     .comment("comment")
                     .swissLineNumber("swissLineNumber")
                     .build();
    version.setSublineVersions(new HashSet<>(
        Set.of(SublineVersion.builder().slnid("ch:1:slnid:100000").version(version).build(),
            SublineVersion.builder().slnid("ch:1:slnid:100001").version(version).build())));
    version = versionRepository.save(version);
  }

  @Test
  void shouldGetSimpleVersion() {
    //given
    version.getSublineVersions().clear();

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
    version.getSublineVersions()
           .add(SublineVersion.builder().slnid("ch:1:slnid:100002").version(version).build());
    versionRepository.save(version);

    //when
    Version result = versionRepository.findAll().get(0);

    //then
    assertThat(result.getSublineVersions()).hasSize(3).extracting("id").isNotNull();
  }

  @Test
  void shouldUpdateVersionDeletingLineRelation() {
    //given
    version.getSublineVersions().remove(version.getSublineVersions().iterator().next());
    versionRepository.save(version);

    //when
    Version result = versionRepository.findAll().get(0);

    //then
    assertThat(result.getSublineVersions()).hasSize(1).extracting("id").isNotNull();
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