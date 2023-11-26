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

 class TimetableFieldNumberServiceScenario6Test extends BaseTimetableFieldNumberServiceTest {

  @Autowired
   TimetableFieldNumberServiceScenario6Test(
      TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberService timetableFieldNumberService) {
    super(versionRepository, timetableFieldNumberService);
  }

  /**
   * Szenario 6: Neue Version in der Zukunft, die letzte Version überschneidet
   *
   * NEU:                             |________________________________
   * IST:      |-------------------------------------------------------
   * Version:                               1
   *
   * RESULTAT: |----------------------|________________________________     NEUE VERSION EINGEFÜGT
   * Version:        1                               2
   *
   */
  @Test
   void scenario6() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = version3Builder().build();
    editedVersion.setDescription("FPFN Description <changed>");
    editedVersion.setComment("Scenario 6");
    editedVersion.setValidFrom(LocalDate.of(2024, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2024, 12, 31));
    editedVersion.getLineRelations()
                 .add(TimetableFieldLineRelation.builder().slnid("ch:1:ttfnid:111111").timetableFieldNumberVersion(version3).build());

    //when
    timetableFieldNumberService.updateVersion(version3, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(4);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // first version no changes
    assertThat(result.get(0)).isNotNull();
    TimetableFieldNumberVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("BEX1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // second version no changes
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("BEX2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // third version update
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 5, 31));
    assertThat(thirdTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(thirdTemporalVersion.getComment()).isNull();
    Set<TimetableFieldLineRelation> lineRelationsThirdVersion = thirdTemporalVersion.getLineRelations();
    assertThat(lineRelationsThirdVersion).isEmpty();
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("BEX3");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    //fourth new
    TimetableFieldNumberVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 6, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fourthTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(fourthTemporalVersion.getComment()).isEqualTo("Scenario 6");
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

  }

  /**
   * Szenario 6: Neue Version in der Zukunft, die letzte Version überschneidet
   *
   * NEU:                             |________________________________
   * IST:      |-------------------------------------------------------
   * Version:                               1
   *
   * RESULTAT: |----------------------|________________________________     NEUE VERSION EINGEFÜGT
   * Version:        1                               2
   *
   */
  @Test
   void scenario6WithOnlyOneVersion() {
    //given
    version1 = versionRepository.save(version1);
    TimetableFieldNumberVersion editedVersion = version1Builder().build();
    editedVersion.setDescription("FPFN Description <changed>");
    editedVersion.setComment("Scenario 6");
    editedVersion.setValidFrom(LocalDate.of(2020, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2021, 12, 31));
    editedVersion.getLineRelations()
                 .add(TimetableFieldLineRelation.builder().slnid("ch:1:ttfnid:111111").timetableFieldNumberVersion(version1).build());

    //when
    timetableFieldNumberService.updateVersion(version1, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // first version no changes
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

    //second new
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 6, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(secondTemporalVersion.getComment()).isEqualTo("Scenario 6");
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

  }

  /**
   * Szenario 6: Neue Version in der Zukunft, die letzte Version überschneidet
   *
   * NEU:                             |________________________________
   * IST:      |-------------------------------------------------------
   * Version:                               1
   *
   * RESULTAT: |----------------------|________________________________     NEUE VERSION EINGEFÜGT
   * Version:        1                               2
   *
   */
  @Test
   void scenario6WhenEditedValidToIsBiggerThenCurrentValidTo() {
    //given
    version1 = versionRepository.save(version1);
    TimetableFieldNumberVersion editedVersion = version1Builder().build();
    editedVersion.setDescription("FPFN Description <changed>");
    editedVersion.setComment("Scenario 6");
    editedVersion.setValidFrom(LocalDate.of(2020, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2022, 12, 31));
    editedVersion.getLineRelations()
                 .add(TimetableFieldLineRelation.builder().slnid("ch:1:ttfnid:111111").timetableFieldNumberVersion(version1).build());

    //when
    timetableFieldNumberService.updateVersion(version1, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // first version no changes
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

    //second new
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 6, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2022, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(secondTemporalVersion.getComment()).isEqualTo("Scenario 6");
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

  }

  /**
   * Szenario 6: Neue Version in der Zukunft, die letzte Version überschneidet
   *
   * NEU:                             |____________________________________
   * IST:      |-------------------------------------------------------
   * Version:                               1
   *
   * RESULTAT: |----------------------|____________________________________     NEUE VERSION EINGEFÜGT
   * Version:        1                               2
   *
   */
  @Test
   void scenario6WhenOnlyValidFromIsEdited() {
    //given
    version1 = versionRepository.save(version1);
    TimetableFieldNumberVersion editedVersion = version1Builder().build();
    editedVersion.setDescription("FPFN Description <changed>");
    editedVersion.setComment("Scenario 6");
    editedVersion.setValidFrom(LocalDate.of(2020, 6, 1));
    editedVersion.setValidTo(version1.getValidTo());
    editedVersion.getLineRelations()
                 .add(TimetableFieldLineRelation.builder().slnid("ch:1:ttfnid:111111").timetableFieldNumberVersion(version1).build());

    //when
    timetableFieldNumberService.updateVersion(version1, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // first version no changes
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

    //second new
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 6, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(secondTemporalVersion.getComment()).isEqualTo("Scenario 6");
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

  }

}
