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

class TimetableFieldNumberServiceScenario3Test extends BaseTimetableFieldNumberServiceTest {

  @Autowired
  TimetableFieldNumberServiceScenario3Test(
      TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberService timetableFieldNumberService) {
    super(versionRepository, timetableFieldNumberService);
  }

  /**
   * Szenario 3: Update, dass über Versionsgrenze geht
   * NEU:                                   |___________|
   * IST:      |-----------|----------------------|--------------------
   * Version:        1                 2                  3
   * <p>
   * RESULTAT: |-----------|----------------|______|_____|-------------     NEUE VERSION EINGEFÜGT
   * Version:        1                 2        4     5          3
   */
  @Test
  void scenario3UpdateVersion2() {
    //given
    version1.setBusinessOrganisation("SBB1");
    version1 = versionRepository.save(version1);
    version2.setBusinessOrganisation("SBB2");
    version2 = versionRepository.save(version2);
    version3.setBusinessOrganisation("SBB3");
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = version2Builder().build();
    editedVersion.setBusinessOrganisation("SBB2");
    editedVersion.setDescriptionOutwardLine1("FPFN Description <changed>");
    editedVersion.setValidFrom(LocalDate.of(2023, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
    editedVersion.getLineRelations()
        .add(TimetableFieldLineRelation.builder().slnid("ch:1:ttfnid:111111").timetableFieldNumberVersion(version2).build());
    editedVersion.setVersion(version2.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version2, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).hasSize(5);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));
    assertThat(result.getFirst()).isNotNull();

    // not touched
    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("SBB1");
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);

    // first current index updated
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 5, 31));
    assertThat(secondTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("SBB2");
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);

    //new
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2023, 6, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(thirdTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("SBB2");
    Set<TimetableFieldLineRelation> lineRelationsThirdVersion = thirdTemporalVersion.getLineRelations();
    assertThat(lineRelationsThirdVersion).hasSize(1);
    TimetableFieldLineRelation lineRelationThirdVersion = lineRelationsThirdVersion.stream().iterator().next();
    assertThat(lineRelationThirdVersion).isNotNull();
    assertThat(lineRelationThirdVersion.getSlnid()).isEqualTo("ch:1:ttfnid:111111");
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);

    //new
    TimetableFieldNumberVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
    assertThat(fourthTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("SBB3");
    Set<TimetableFieldLineRelation> lineRelationsFourthVersion = fourthTemporalVersion.getLineRelations();
    assertThat(lineRelationsFourthVersion).hasSize(1);
    TimetableFieldLineRelation lineRelationFourthVersion = lineRelationsFourthVersion.stream().iterator().next();
    assertThat(lineRelationFourthVersion).isNotNull();
    assertThat(lineRelationFourthVersion.getSlnid()).isEqualTo("ch:1:ttfnid:111111");
    assertThat(fourthTemporalVersion.getNumber()).isEqualTo("80.099.3");
    assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fourthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);

    //last current index updated
    TimetableFieldNumberVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 6, 2));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fifthTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(fifthTemporalVersion.getBusinessOrganisation()).isEqualTo("SBB3");
    assertThat(fifthTemporalVersion.getLineRelations()).isEmpty();
    assertThat(fifthTemporalVersion.getNumber()).isEqualTo("80.099.3");
    assertThat(fifthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fifthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
  }

  /**
   * Szenario 3: Update, dass über Versionsgrenze geht
   * NEU:                                   |___________|
   * IST:      |-----------|----------------------|--------------------
   * Version:        1                 2                  3
   * <p>
   * RESULTAT: |-----------|----------------|______|_____|-------------     NEUE VERSION EINGEFÜGT
   * Version:        1                 2        4     5          3
   */
  @Test
  void scenario3UpdateVersion3() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = version3Builder().build();
    editedVersion.setDescriptionOutwardLine1("FPFN Description <changed>");
    editedVersion.setValidFrom(LocalDate.of(2023, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
    editedVersion.getLineRelations()
        .add(TimetableFieldLineRelation.builder().slnid("ch:1:ttfnid:111111").timetableFieldNumberVersion(version3).build());
    editedVersion.setVersion(version3.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version3, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).hasSize(5);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));
    assertThat(result.getFirst()).isNotNull();

    // not touched
    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // first current index updated
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 5, 31));
    assertThat(secondTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    //new
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2023, 6, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(thirdTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    Set<TimetableFieldLineRelation> lineRelationsThirdVersion = thirdTemporalVersion.getLineRelations();
    assertThat(lineRelationsThirdVersion).hasSize(1);
    TimetableFieldLineRelation lineRelationThirdVersion = lineRelationsThirdVersion.stream().iterator().next();
    assertThat(lineRelationThirdVersion).isNotNull();
    assertThat(lineRelationThirdVersion.getSlnid()).isEqualTo("ch:1:ttfnid:111111");
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    //new
    TimetableFieldNumberVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
    assertThat(fourthTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    Set<TimetableFieldLineRelation> lineRelationsFourthVersion = fourthTemporalVersion.getLineRelations();
    assertThat(lineRelationsFourthVersion).hasSize(1);
    TimetableFieldLineRelation lineRelationFourthVersion = lineRelationsFourthVersion.stream().iterator().next();
    assertThat(lineRelationFourthVersion).isNotNull();
    assertThat(lineRelationFourthVersion.getSlnid()).isEqualTo("ch:1:ttfnid:111111");
    assertThat(fourthTemporalVersion.getNumber()).isEqualTo("80.099.3");
    assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fourthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    //last current index updated
    TimetableFieldNumberVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 6, 2));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fifthTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(fifthTemporalVersion.getLineRelations()).isEmpty();
    assertThat(fifthTemporalVersion.getNumber()).isEqualTo("80.099.3");
    assertThat(fifthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fifthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(fifthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
  }

}
