package ch.sbb.timetable.field.number.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.IntegrationTest;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.repository.VersionRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class VersionServiceTest {

  private final VersionRepository versionRepository;
  private final VersionService versionService;

  private Version version1;
  private Version version2;

  @BeforeEach
  void init() {
    version1 = Version.builder().ttfnid("ch:1:fpfnid:100000")
                      .name("FPFN Name")
                      .number("BEX")
                      .swissTimetableFieldNumber("b0.BEX")
                      .validFrom(LocalDate.of(2020, 1, 1))
                      .validTo(LocalDate.of(2021, 12, 31))
                      .build();
    version1 = versionRepository.save(version1);
    version2 = Version.builder().ttfnid("ch:1:fpfnid:100000")
                      .name("FPFN Name")
                      .number("BEX")
                      .swissTimetableFieldNumber("b0.BEX")
                      .validFrom(LocalDate.of(2022, 1, 1))
                      .validTo(LocalDate.of(2023, 12, 13))
                      .build();
    version2 = versionRepository.save(version2);
  }

  @AfterEach
  void cleanUp() {
    List<Version> versionsVersioned = versionRepository.getAllVersionsVersioned(
        version1.getTtfnid());
    versionRepository.deleteAll(versionsVersioned);
  }

  @Autowired
  public VersionServiceTest(VersionRepository versionRepository,
      VersionService versionService) {
    this.versionRepository = versionRepository;
    this.versionService = versionService;
  }

  @Test
  public void shouldUpdateVersionWhenValidFromAndValidToAreNotModified() {
    //given
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");

    //when
    versionService.updateVersion(version1, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());
    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
    result.sort(Comparator.comparing(Version::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");

    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 13));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name");

  }

  /**
   * Input:
   * |------------------------|------------------------|
   *            1                         2
   * Output:
   * |--------------|---------|------------------------|
   *        1           3                2
   */
  @Test
  public void shouldAddNewVersionWhenValidFromIsModified() {
    //given
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setValidFrom(LocalDate.of(2020, 6, 1));

    //when
    versionService.updateVersion(version1, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(3);
    result.sort(Comparator.comparing(Version::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 5, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name");

    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 6, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");

    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 13));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name");

  }

  /** Scenario 1c
   * Input:
   *      |------------------------|------------------------|
   *            1                         2
   * Output:
   * |-----------------------------|------------------------|
   *        1                           2
   */
  @Test
  public void shouldUpdateFirstTemporalVersionWhenValidFromIsModifiedAndBeforeFirstTemporalVersion() {
    //given
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setValidFrom(LocalDate.of(2019, 6, 1));

    //when
    versionService.updateVersion(version1, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
    result.sort(Comparator.comparing(Version::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    Version secondTemporalVersion = result.get(0);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2019, 6, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");

    Version thirdTemporalVersion = result.get(1);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 13));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name");

  }



}