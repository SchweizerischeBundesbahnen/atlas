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

 class TimetableFieldNumberServiceScenario4Test extends BaseTimetableFieldNumberServiceTest {

  @Autowired
   TimetableFieldNumberServiceScenario4Test(
      TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberService timetableFieldNumberService) {
    super(versionRepository, timetableFieldNumberService);
  }

  /**
   * Szenario 4: Update, das über eine ganze Version hinausragt
   * NEU:             |___________________________________|
   * IST:      |-----------|----------------------|--------------------
   * Version:        1                 2                  3
   *
   *
   * RESULTAT: |------|_____|______________________|______|------------     NEUE VERSION EINGEFÜGT
   * Version:      1     4              2              5        3
   */
  @Test
   void scenario4() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = version3Builder().build();
    editedVersion.setDescription("FPFN Description <changed>");
    editedVersion.setComment("Scenario 4");
    editedVersion.setValidFrom(LocalDate.of(2020, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
    editedVersion.getLineRelations()
        .add(TimetableFieldLineRelation.builder().slnid("ch:1:ttfnid:111111").timetableFieldNumberVersion(version3).build());

    //when
    timetableFieldNumberService.updateVersion(version3, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).hasSize(5);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    // first current index updated
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
    assertThat(secondTemporalVersion.getComment()).isEqualTo("Scenario 4");
    assertThat(secondTemporalVersion.getLineRelations()).isNotEmpty();
    Set<TimetableFieldLineRelation> lineRelationsSecondVersion = secondTemporalVersion.getLineRelations();
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
    assertThat(thirdTemporalVersion.getComment()).isEqualTo("Scenario 4");
    Set<TimetableFieldLineRelation> lineRelationsThirdVersion = thirdTemporalVersion.getLineRelations();
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
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
    assertThat(fourthTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(fourthTemporalVersion.getComment()).isEqualTo("Scenario 4");
    Set<TimetableFieldLineRelation> lineRelationsFourthVersion = fourthTemporalVersion.getLineRelations();
    assertThat(lineRelationsFourthVersion).hasSize(1);
    TimetableFieldLineRelation lineRelationFourthVersion = lineRelationsFourthVersion.stream().iterator().next();
    assertThat(lineRelationFourthVersion).isNotNull();
    assertThat(lineRelationFourthVersion.getSlnid()).isEqualTo("ch:1:ttfnid:111111");
    assertThat(fourthTemporalVersion.getNumber()).isEqualTo("BEX3");
    assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fourthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    //last current index updated
    TimetableFieldNumberVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 6, 2));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fifthTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(fifthTemporalVersion.getComment()).isNull();
    assertThat(fifthTemporalVersion.getLineRelations()).isEmpty();
    assertThat(fifthTemporalVersion.getNumber()).isEqualTo("BEX3");
    assertThat(fifthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fifthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(fifthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

  }

}
