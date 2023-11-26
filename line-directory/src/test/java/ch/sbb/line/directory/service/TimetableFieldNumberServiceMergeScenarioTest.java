package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.entity.TimetableFieldLineRelation;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

 class TimetableFieldNumberServiceMergeScenarioTest extends
    BaseTimetableFieldNumberServiceTest {

  @Autowired
   TimetableFieldNumberServiceMergeScenarioTest(
      TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberService timetableFieldNumberService) {
    super(versionRepository, timetableFieldNumberService);
  }

  /**
   * Merge zwei versionen
   *
   * NEU:                            |__________|
   *                                  description=SBB3
   * IST:      |----------|----------|----------|----------|
   * Version:        1          2          3           4
   * Änderung:  description=SBB1  description=SBB2  description=SBB3  description=SBB4
   *
   * RESULTAT: |----------|---------------------|----------|
   * Version:        1               2               4
   * Änderung:  description=SBB1  description=SBB2  description=SBB4
   */
  @Test
   void scenarioMergeTwoVersions() {
    //given
    version1.setDescription("SBB1");
    version1.setNumber("BEX");
    version1 = versionRepository.save(version1);
    version2.setDescription("SBB2");
    version2.setNumber("BEX");
    version2 = versionRepository.save(version2);
    version3.setDescription("SBB3");
    version3.setNumber("BEX");
    version3 = versionRepository.save(version3);
    version4.setDescription("SBB4");
    version4.setNumber("BEX");
    version4 = versionRepository.save(version4);
    TimetableFieldNumberVersion editedVersion = version3Builder().build();
    editedVersion.setNumber("BEX");
    editedVersion.setDescription("SBB2");

    //when
    timetableFieldNumberService.updateVersion(version3, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(3);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // first version no changes
    assertThat(result.get(0)).isNotNull();
    TimetableFieldNumberVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescription()).isEqualTo("SBB1");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("BEX");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    // second merged with third
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("SBB2");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("BEX");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    // third version no changes
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(thirdTemporalVersion.getDescription()).isEqualTo("SBB4");
    Set<TimetableFieldLineRelation> lineRelationsThirdVersion = thirdTemporalVersion.getLineRelations();
    assertThat(lineRelationsThirdVersion).isEmpty();
    assertThat(thirdTemporalVersion.getComment()).isNull();
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("BEX");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

  }

  /**
   * Merge drei versionen
   *
   * NEU:                 |__________|
   *                       description=SBB1
   * IST:      |----------|----------|----------|
   * Version:        1          2          3
   * Änderung:  description=SBB1  description=SBB2  description=SBB1
   *
   * RESULTAT: |--------------------------------|
   * Version:                 1
   * Änderung:            description=SBB1
   */
  @Test
   void scenarioMergeAllVersions() {
    //given
    version1.setDescription("SBB1");
    version1.setNumber("BEX");
    version1 = versionRepository.save(version1);
    version2.setDescription("SBB2");
    version2.setNumber("BEX");
    version2 = versionRepository.save(version2);
    version3.setDescription("SBB1");
    version3.setNumber("BEX");
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = version2Builder().build();
    editedVersion.setDescription("SBB2");
    editedVersion.setNumber("BEX");
    editedVersion.setDescription("SBB1");

    //when
    timetableFieldNumberService.updateVersion(version2, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // result version merging version1, version2 and version3
    assertThat(result.get(0)).isNotNull();
    TimetableFieldNumberVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(firstTemporalVersion.getDescription()).isEqualTo("SBB1");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("BEX");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

  }

  /**
   * Merge über mehrere versionen
   *
   * NEU:                 |______________________|
   *                              description=SBB1
   * IST:      |----------|----------|----------|----------|----------|
   * Version:        1          2          3          4         5
   * Änderung:  description=SBB1  description=SBB2  description=SBB3  description=SBB1  description=SBB4
   *
   * RESULTAT: |-------------------------------------------|----------|
   * Version:                 1                                 2
   * Änderung:            description=SBB1                         description=SBB4
   */
  @Test
   void scenarioMergeThroughMultipleVersions() {
    //given
    version1.setDescription("SBB1");
    version1.setNumber("BEX");
    version1 = versionRepository.save(version1);
    version2.setDescription("SBB2");
    version2.setNumber("BEX");
    version2 = versionRepository.save(version2);
    version3.setDescription("SBB3");
    version3.setNumber("BEX");
    version3 = versionRepository.save(version3);
    version4.setDescription("SBB1");
    version4.setNumber("BEX");
    version4 = versionRepository.save(version4);
    version5.setNumber("BEX");
    version5.setDescription("SBB4");
    version5 = versionRepository.save(version5);
    TimetableFieldNumberVersion editedVersion = version2Builder().build();
    editedVersion.setNumber("BEX");
    editedVersion.setDescription("SBB1");
    editedVersion.setValidFrom(version2.getValidFrom());
    editedVersion.setValidTo(version3.getValidTo());

    //when
    timetableFieldNumberService.updateVersion(version2, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // result version merging version1, version2, version3 and version4
    TimetableFieldNumberVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(firstTemporalVersion.getDescription()).isEqualTo("SBB1");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("BEX");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    //second not touched
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2026, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2026, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("SBB4");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("BEX");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

  }

  /**
   * Merge über mehrere versionen
   *
   * NEU:                 |_____________________|
   *                              description=SBB1
   * IST:      |----------|----------|----------|  |----------|----------|
   * Version:        1          2         3              4         5
   * Änderung:  description=SBB1  description=SBB2  description=SBB3     description=SBB1  description=SBB4
   *
   * RESULTAT: |--------------------------------|  |---------|----------|
   * Version:                 1                         2        3
   * Änderung:            description=SBB1                 description=SBB1  description=SBB4
   */
  @Test
   void scenarioMergeThroughMultipleVersionsWithInterruption() {
    //given
    version1.setDescription("SBB1");
    version1.setNumber("BEX");
    version1 = versionRepository.save(version1);
    version2.setDescription("SBB2");
    version2.setNumber("BEX");
    version2 = versionRepository.save(version2);
    version3.setDescription("SBB3");
    version3.setNumber("BEX");
    version3.setValidTo(LocalDate.of(2024, 6, 1));
    version3 = versionRepository.save(version3);
    version4.setDescription("SBB1");
    version4.setNumber("BEX");
    version4 = versionRepository.save(version4);
    version5.setNumber("BEX");
    version5.setDescription("SBB4");
    version5 = versionRepository.save(version5);
    TimetableFieldNumberVersion editedVersion = version2Builder().build();
    editedVersion.setNumber("BEX");
    editedVersion.setDescription("SBB1");
    editedVersion.setValidFrom(version2.getValidFrom());
    editedVersion.setValidTo(version3.getValidTo());

    //when
    timetableFieldNumberService.updateVersion(version2, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(3);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // result version merging version1, version2, version3 and version4
    TimetableFieldNumberVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
    assertThat(firstTemporalVersion.getDescription()).isEqualTo("SBB1");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("BEX");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    //second not touched
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("SBB1");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("BEX");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    //third not touched
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2026, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2026, 12, 31));
    assertThat(thirdTemporalVersion.getDescription()).isEqualTo("SBB4");
    assertThat(thirdTemporalVersion.getComment()).isNull();
    assertThat(thirdTemporalVersion.getLineRelations()).isEmpty();
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("BEX");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

  }

}
