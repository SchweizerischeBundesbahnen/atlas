package ch.sbb.timetable.field.number.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.entity.LineRelation;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.repository.VersionRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class VersionServiceMergeScenarioTest extends BaseVersionServiceTest {

  @Autowired
  public VersionServiceMergeScenarioTest(
      VersionRepository versionRepository,
      VersionService versionService) {
    super(versionRepository, versionService);
  }

  /**
   * Merge zwei versionen
   *
   * NEU:                            |__________|
   *                                  name=SBB3
   * IST:      |----------|----------|----------|----------|
   * Version:        1          2          3           4
   * Änderung:  name=SBB1  name=SBB2  name=SBB3  name=SBB4
   *
   * RESULTAT: |----------|---------------------|----------|
   * Version:        1               2               4
   * Änderung:  name=SBB1       name=SBB2        name=SBB4
   */
  @Test
  public void scenarioMergeTwoVersions() {
    //given
    version1.setName("SBB1");
    version1.setNumber("BEX");
    version1 = versionRepository.save(version1);
    version2.setName("SBB2");
    version2.setNumber("BEX");
    version2 = versionRepository.save(version2);
    version3.setName("SBB3");
    version3.setNumber("BEX");
    version3 = versionRepository.save(version3);
    version4.setName("SBB4");
    version4.setNumber("BEX");
    version4 = versionRepository.save(version4);
    Version editedVersion = new Version();
    editedVersion.setName("SBB2");

    //when
    versionService.updateVersion(version3, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(3);
    result.sort(Comparator.comparing(Version::getValidFrom));

    // first version no changes
    assertThat(result.get(0)).isNotNull();
    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("SBB1");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();

    // second merged with third
    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("SBB2");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();

    // third version no changes
    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("SBB4");
    Set<LineRelation> lineRelationsThirdVersion = thirdTemporalVersion.getLineRelations();
    assertThat(lineRelationsThirdVersion).isEmpty();

  }

  /**
   * Merge drei versionen
   *
   * NEU:                 |__________|
   *                       name=SBB1
   * IST:      |----------|----------|----------|
   * Version:        1          2          3
   * Änderung:  name=SBB1  name=SBB2  name=SBB1
   *
   * RESULTAT: |--------------------------------|
   * Version:                 1
   * Änderung:            name=SBB1
   */
  @Test
  public void scenarioMergeAllVersions() {
    //given
    version1.setName("SBB1");
    version1.setNumber("BEX");
    version1 = versionRepository.save(version1);
    version2.setName("SBB2");
    version2.setNumber("BEX");
    version2 = versionRepository.save(version2);
    version3.setName("SBB1");
    version3.setNumber("BEX");
    version3 = versionRepository.save(version3);
    Version editedVersion = new Version();
    editedVersion.setName("SBB1");

    //when
    versionService.updateVersion(version2, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(1);
    result.sort(Comparator.comparing(Version::getValidFrom));

    // result version merging version1, version2 and version3
    assertThat(result.get(0)).isNotNull();
    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("SBB1");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();

  }

  /**
   * Merge über mehrere versionen
   *
   * NEU:                 |______________________|
   *                              name=SBB1
   * IST:      |----------|----------|----------|----------|----------|
   * Version:        1          2          3          4         5
   * Änderung:  name=SBB1  name=SBB2  name=SBB3  name=SBB1  name=SBB4
   *
   *
   *
   * RESULTAT: |-------------------------------------------|----------|
   * Version:                 1                                 2
   * Änderung:            name=SBB1                         name=SBB4
   */
  @Test
  public void scenarioMergeThroughMultipleVersions() {
    //given
    version1.setName("SBB1");
    version1.setNumber("BEX");
    version1 = versionRepository.save(version1);
    version2.setName("SBB2");
    version2.setNumber("BEX");
    version2 = versionRepository.save(version2);
    version3.setName("SBB3");
    version3.setNumber("BEX");
    version3 = versionRepository.save(version3);
    version4.setName("SBB1");
    version4.setNumber("BEX");
    version4 = versionRepository.save(version4);
    version5.setNumber("BEX");
    version5.setName("SBB4");
    version5 = versionRepository.save(version5);
    Version editedVersion = new Version();
    editedVersion.setName("SBB1");
    editedVersion.setValidFrom(version2.getValidFrom());
    editedVersion.setValidTo(version3.getValidTo());

    //when
    versionService.updateVersion(version2, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
    result.sort(Comparator.comparing(Version::getValidFrom));

    // result version merging version1, version2, version3 and version4
    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("SBB1");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();

    //second not touched
    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2026, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2026, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("SBB4");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();

  }

}