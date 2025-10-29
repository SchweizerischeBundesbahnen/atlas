package ch.sbb.line.directory.module.ttfn.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldLineRelation;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.module.ttfn.repository.TimetableFieldNumberVersionRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TimetableFieldNumberServiceMergeScenarioTest extends BaseTimetableFieldNumberServiceTest {

  private static final String NUMBER = "10.099";

  @Autowired
  TimetableFieldNumberServiceMergeScenarioTest(
      TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberService timetableFieldNumberService) {
    super(versionRepository, timetableFieldNumberService);
  }

  /**
   * Merge zwei versionen
   * <p>
   * NEU:                            |__________|
   * description=SBB3
   * IST:      |----------|----------|----------|----------|
   * Version:        1          2          3           4
   * Änderung:  description=SBB1  description=SBB2  description=SBB3  description=SBB4
   * <p>
   * RESULTAT: |----------|---------------------|----------|
   * Version:        1               2               4
   * Änderung:  description=SBB1  description=SBB2  description=SBB4
   */
  @Test
  void scenarioMergeTwoVersions() {
    //given
    version1.setDescriptionOutwardLine1("SBB1");
    version1.setNumber(NUMBER);
    version1 = versionRepository.save(version1);
    version2.setDescriptionOutwardLine1("SBB2");
    version2.setNumber(NUMBER);
    version2 = versionRepository.save(version2);
    version3.setDescriptionOutwardLine1("SBB3");
    version3.setNumber(NUMBER);
    version3 = versionRepository.save(version3);
    version4.setDescriptionOutwardLine1("SBB4");
    version4.setNumber(NUMBER);
    version4 = versionRepository.save(version4);
    TimetableFieldNumberVersion editedVersion = version3Builder().build();
    editedVersion.setNumber(NUMBER);
    editedVersion.setDescriptionOutwardLine1("SBB2");
    editedVersion.setVersion(version3.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version3, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).hasSize(3);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // first version no changes
    assertThat(result.getFirst()).isNotNull();
    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("SBB1");
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo(NUMBER);
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);

    // second merged with third
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(secondTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("SBB2");
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo(NUMBER);
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);

    // third version no changes
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(thirdTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("SBB4");
    Set<TimetableFieldLineRelation> lineRelationsThirdVersion = thirdTemporalVersion.getLineRelations();
    assertThat(lineRelationsThirdVersion).isEmpty();
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo(NUMBER);
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
  }

  /**
   * Merge drei versionen
   * <p>
   * NEU:                 |__________|
   * description=SBB1
   * IST:      |----------|----------|----------|
   * Version:        1          2          3
   * Änderung:  description=SBB1  description=SBB2  description=SBB1
   * <p>
   * RESULTAT: |--------------------------------|
   * Version:                 1
   * Änderung:            description=SBB1
   */
  @Test
  void scenarioMergeAllVersions() {
    //given
    version1.setDescriptionOutwardLine1("SBB1");
    version1.setNumber(NUMBER);
    version1 = versionRepository.save(version1);
    version2.setDescriptionOutwardLine1("SBB2");
    version2.setNumber(NUMBER);
    version2 = versionRepository.save(version2);
    version3.setDescriptionOutwardLine1("SBB1");
    version3.setNumber(NUMBER);
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = version2Builder().build();
    editedVersion.setDescriptionOutwardLine1("SBB2");
    editedVersion.setNumber(NUMBER);
    editedVersion.setDescriptionOutwardLine1("SBB1");
    editedVersion.setVersion(version2.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version2, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).hasSize(1);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // result version merging version1, version2 and version3
    assertThat(result.getFirst()).isNotNull();
    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("SBB1");
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo(NUMBER);
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
  }

  /**
   * Merge über mehrere versionen
   * <p>
   * NEU:                 |______________________|
   * description=SBB1
   * IST:      |----------|----------|----------|----------|----------|
   * Version:        1          2          3          4         5
   * Änderung:  description=SBB1  description=SBB2  description=SBB3  description=SBB1  description=SBB4
   * <p>
   * RESULTAT: |-------------------------------------------|----------|
   * Version:                 1                                 2
   * Änderung:            description=SBB1                         description=SBB4
   */
  @Test
  void scenarioMergeThroughMultipleVersions() {
    //given
    version1.setDescriptionOutwardLine1("SBB1");
    version1.setNumber(NUMBER);
    version1 = versionRepository.save(version1);
    version2.setDescriptionOutwardLine1("SBB2");
    version2.setNumber(NUMBER);
    version2 = versionRepository.save(version2);
    version3.setDescriptionOutwardLine1("SBB3");
    version3.setNumber(NUMBER);
    version3 = versionRepository.save(version3);
    version4.setDescriptionOutwardLine1("SBB1");
    version4.setNumber(NUMBER);
    version4 = versionRepository.save(version4);
    version5.setNumber(NUMBER);
    version5.setDescriptionOutwardLine1("SBB4");
    version5 = versionRepository.save(version5);
    TimetableFieldNumberVersion editedVersion = version2Builder().build();
    editedVersion.setNumber(NUMBER);
    editedVersion.setDescriptionOutwardLine1("SBB1");
    editedVersion.setValidFrom(version2.getValidFrom());
    editedVersion.setValidTo(version3.getValidTo());
    editedVersion.setVersion(version2.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version2, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).hasSize(2);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // result version merging version1, version2, version3 and version4
    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("SBB1");
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo(NUMBER);
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);

    //second not touched
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2026, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2026, 12, 31));
    assertThat(secondTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("SBB4");
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo(NUMBER);
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
  }

  /**
   * Merge über mehrere versionen
   * <p>
   * NEU:                 |_____________________|
   * description=SBB1
   * IST:      |----------|----------|----------|  |----------|----------|
   * Version:        1          2         3              4         5
   * Änderung:  description=SBB1  description=SBB2  description=SBB3     description=SBB1  description=SBB4
   * <p>
   * RESULTAT: |--------------------------------|  |---------|----------|
   * Version:                 1                         2        3
   * Änderung:            description=SBB1                 description=SBB1  description=SBB4
   */
  @Test
  void scenarioMergeThroughMultipleVersionsWithInterruption() {
    //given
    version1.setDescriptionOutwardLine1("SBB1");
    version1.setNumber(NUMBER);
    version1 = versionRepository.save(version1);
    version2.setDescriptionOutwardLine1("SBB2");
    version2.setNumber(NUMBER);
    version2 = versionRepository.save(version2);
    version3.setDescriptionOutwardLine1("SBB3");
    version3.setNumber(NUMBER);
    version3.setValidTo(LocalDate.of(2024, 6, 1));
    version3 = versionRepository.save(version3);
    version4.setDescriptionOutwardLine1("SBB1");
    version4.setNumber(NUMBER);
    version4 = versionRepository.save(version4);
    version5.setNumber(NUMBER);
    version5.setDescriptionOutwardLine1("SBB4");
    version5 = versionRepository.save(version5);
    TimetableFieldNumberVersion editedVersion = version2Builder().build();
    editedVersion.setNumber(NUMBER);
    editedVersion.setDescriptionOutwardLine1("SBB1");
    editedVersion.setValidFrom(version2.getValidFrom());
    editedVersion.setValidTo(version3.getValidTo());
    editedVersion.setVersion(version2.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version2, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).hasSize(3);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // result version merging version1, version2, version3 and version4
    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("SBB1");
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo(NUMBER);
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);

    //second not touched
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(secondTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("SBB1");
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo(NUMBER);
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);

    //third not touched
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2026, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2026, 12, 31));
    assertThat(thirdTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("SBB4");
    assertThat(thirdTemporalVersion.getLineRelations()).isEmpty();
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo(NUMBER);
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
  }

}
