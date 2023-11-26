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

 class TimetableFieldNumberServiceScenario5Test extends BaseTimetableFieldNumberServiceTest {

  @Autowired
   TimetableFieldNumberServiceScenario5Test(
      TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberService timetableFieldNumberService) {
    super(versionRepository, timetableFieldNumberService);
  }

  /**
   * Szenario 5: Update, das über mehrere Versionen hinausragt
   *
   * NEU:             |___________________________________|
   * IST:      |-----------|-----------|-----------|-------------------
   * Version:        1           2          3               4
   *
   * RESULTAT: |------|_____|__________|____________|_____|------------     NEUE VERSION EINGEFÜGT
   * Version:      1     5       2           3         6      4
   */
  @Test
   void scenario5() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    version4 = versionRepository.save(version4);
    TimetableFieldNumberVersion editedVersion = version3Builder().build();
    editedVersion.setDescription("FPFN Description <changed>");
    editedVersion.setComment("Scenario 5");
    editedVersion.setValidFrom(LocalDate.of(2020, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2025, 6, 1));
    editedVersion.getLineRelations()
                 .add(TimetableFieldLineRelation.builder().slnid("ch:1:ttfnid:111111").timetableFieldNumberVersion(version3).build());

    //when
    timetableFieldNumberService.updateVersion(version3, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(6);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // first current index updated
    assertThat(result.get(0)).isNotNull();
    TimetableFieldNumberVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 5, 31));
    assertThat(firstTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("BEX1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // new
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 6, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(secondTemporalVersion.getComment()).isEqualTo("Scenario 5");
    assertThat(secondTemporalVersion.getLineRelations()).isNotEmpty();
    Set<TimetableFieldLineRelation> lineRelationsSecondVersion = secondTemporalVersion.getLineRelations();
    assertThat(lineRelationsSecondVersion).isNotEmpty();
    assertThat(lineRelationsSecondVersion).hasSize(1);
    TimetableFieldLineRelation lineRelationSecondVersion = lineRelationsSecondVersion.stream().iterator().next();
    assertThat(lineRelationSecondVersion).isNotNull();
    assertThat(lineRelationSecondVersion.getSlnid()).isEqualTo("ch:1:ttfnid:111111");
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("BEX1");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    //update
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(thirdTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(thirdTemporalVersion.getComment()).isEqualTo("Scenario 5");
    Set<TimetableFieldLineRelation> lineRelationsThirdVersion = thirdTemporalVersion.getLineRelations();
    assertThat(lineRelationsThirdVersion).isNotEmpty();
    assertThat(lineRelationsThirdVersion).hasSize(1);
    TimetableFieldLineRelation lineRelationThirdVersion = lineRelationsThirdVersion.stream().iterator().next();
    assertThat(lineRelationThirdVersion).isNotNull();
    assertThat(lineRelationThirdVersion.getSlnid()).isEqualTo("ch:1:ttfnid:111111");
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("BEX2");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    //new
    TimetableFieldNumberVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fourthTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(fourthTemporalVersion.getComment()).isEqualTo("Scenario 5");
    Set<TimetableFieldLineRelation> lineRelationsFourthVersion = fourthTemporalVersion.getLineRelations();
    assertThat(lineRelationsFourthVersion).isNotEmpty();
    assertThat(lineRelationsFourthVersion).hasSize(1);
    TimetableFieldLineRelation lineRelationFourthVersion = lineRelationsFourthVersion.stream().iterator().next();
    assertThat(lineRelationFourthVersion).isNotNull();
    assertThat(lineRelationFourthVersion.getSlnid()).isEqualTo("ch:1:ttfnid:111111");
    assertThat(fourthTemporalVersion.getNumber()).isEqualTo("BEX3");
    assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fourthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    //new
    TimetableFieldNumberVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 6, 1));
    assertThat(fifthTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(fifthTemporalVersion.getComment()).isEqualTo("Scenario 5");
    Set<TimetableFieldLineRelation> lineRelationsFifthVersion = fifthTemporalVersion.getLineRelations();
    assertThat(lineRelationsFifthVersion).isNotEmpty();
    assertThat(lineRelationsFifthVersion).hasSize(1);
    TimetableFieldLineRelation lineRelationFifthVersion = lineRelationsFifthVersion.stream().iterator().next();
    assertThat(lineRelationFifthVersion).isNotNull();
    assertThat(lineRelationFifthVersion.getSlnid()).isEqualTo("ch:1:ttfnid:111111");
    assertThat(fifthTemporalVersion.getNumber()).isEqualTo("BEX4");
    assertThat(fifthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fifthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(fifthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    //last current index updated
    TimetableFieldNumberVersion sixthTemporalVersion = result.get(5);
    assertThat(sixthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 6, 2));
    assertThat(sixthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(sixthTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(sixthTemporalVersion.getComment()).isNull();
    assertThat(sixthTemporalVersion.getLineRelations()).isEmpty();
    assertThat(sixthTemporalVersion.getNumber()).isEqualTo("BEX4");
    assertThat(sixthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(sixthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(sixthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

  }

}
