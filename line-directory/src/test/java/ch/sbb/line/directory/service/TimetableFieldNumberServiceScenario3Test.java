package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.entity.TimetableFieldLineRelation;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TimetableFieldNumberServiceScenario3Test extends BaseTimetableFieldNumberServiceTest {

  @Autowired
  public TimetableFieldNumberServiceScenario3Test(
      TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberService timetableFieldNumberService) {
    super(versionRepository, timetableFieldNumberService);
  }

  /**
   * Szenario 3: Update, dass über Versionsgrenze geht
   * NEU:                                   |___________|
   * IST:      |-----------|----------------------|--------------------
   * Version:        1                 2                  3
   *
   * RESULTAT: |-----------|----------------|______|_____|-------------     NEUE VERSION EINGEFÜGT
   * Version:        1                 2        4     5          3
   */
  @Test
  public void scenario3UpdateVersion2() {
    //given
    version1.setBusinessOrganisation("SBB1");
    version1 = versionRepository.save(version1);
    version2.setBusinessOrganisation("SBB2");
    version2 = versionRepository.save(version2);
    version3.setBusinessOrganisation("SBB3");
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = new TimetableFieldNumberVersion();
    editedVersion.setDescription("FPFN Description <changed>");
    editedVersion.setComment("Scenario 3");
    editedVersion.setValidFrom(LocalDate.of(2023, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
    editedVersion.getLineRelations()
                 .add(TimetableFieldLineRelation.builder().slnid("ch:1:ttfnid:111111").timetableFieldNumberVersion(version2).build());

    //when
    timetableFieldNumberService.updateVersion(version2, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(5);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    // not touched
    TimetableFieldNumberVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("SBB1");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("BEX1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.ACTIVE);
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    // first current index updated
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 5, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("SBB2");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("BEX2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.ACTIVE);
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    //new
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2023, 6, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(thirdTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(thirdTemporalVersion.getComment()).isEqualTo("Scenario 3");
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("SBB2");
    Set<TimetableFieldLineRelation> lineRelationsThirdVersion = thirdTemporalVersion.getLineRelations();
    assertThat(lineRelationsThirdVersion).isNotEmpty();
    assertThat(lineRelationsThirdVersion.size()).isEqualTo(1);
    TimetableFieldLineRelation lineRelationThirdVersion = lineRelationsThirdVersion.stream().iterator().next();
    assertThat(lineRelationThirdVersion).isNotNull();
    assertThat(lineRelationThirdVersion.getSlnid()).isEqualTo("ch:1:ttfnid:111111");
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("BEX2");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.ACTIVE);
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    //new
    TimetableFieldNumberVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
    assertThat(fourthTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(fourthTemporalVersion.getComment()).isEqualTo("Scenario 3");
    assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("SBB3");
    Set<TimetableFieldLineRelation> lineRelationsFourthVersion = fourthTemporalVersion.getLineRelations();
    assertThat(lineRelationsFourthVersion).isNotEmpty();
    assertThat(lineRelationsFourthVersion.size()).isEqualTo(1);
    TimetableFieldLineRelation lineRelationFourthVersion = lineRelationsFourthVersion.stream().iterator().next();
    assertThat(lineRelationFourthVersion).isNotNull();
    assertThat(lineRelationFourthVersion.getSlnid()).isEqualTo("ch:1:ttfnid:111111");
    assertThat(fourthTemporalVersion.getNumber()).isEqualTo("BEX3");
    assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.ACTIVE);
    assertThat(fourthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    //last current index updated
    TimetableFieldNumberVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 6, 2));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fifthTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(fifthTemporalVersion.getBusinessOrganisation()).isEqualTo("SBB3");
    assertThat(fifthTemporalVersion.getComment()).isNull();
    assertThat(fifthTemporalVersion.getLineRelations()).isEmpty();
    assertThat(fifthTemporalVersion.getNumber()).isEqualTo("BEX3");
    assertThat(fifthTemporalVersion.getStatus()).isEqualTo(Status.ACTIVE);
    assertThat(fifthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

  }

  /**
   * Szenario 3: Update, dass über Versionsgrenze geht
   * NEU:                                   |___________|
   * IST:      |-----------|----------------------|--------------------
   * Version:        1                 2                  3
   *
   * RESULTAT: |-----------|----------------|______|_____|-------------     NEUE VERSION EINGEFÜGT
   * Version:        1                 2        4     5          3
   */
  @Test
  public void scenario3UpdateVersion3() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = new TimetableFieldNumberVersion();
    editedVersion.setDescription("FPFN Description <changed>");
    editedVersion.setComment("Scenario 3");
    editedVersion.setValidFrom(LocalDate.of(2023, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
    editedVersion.getLineRelations()
                 .add(TimetableFieldLineRelation.builder().slnid("ch:1:ttfnid:111111").timetableFieldNumberVersion(version3).build());

    //when
    timetableFieldNumberService.updateVersion(version3, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(5);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    // not touched
    TimetableFieldNumberVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("BEX1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.ACTIVE);
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // first current index updated
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 5, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("BEX2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.ACTIVE);
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    //new
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2023, 6, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(thirdTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(thirdTemporalVersion.getComment()).isEqualTo("Scenario 3");
    Set<TimetableFieldLineRelation> lineRelationsThirdVersion = thirdTemporalVersion.getLineRelations();
    assertThat(lineRelationsThirdVersion).isNotEmpty();
    assertThat(lineRelationsThirdVersion.size()).isEqualTo(1);
    TimetableFieldLineRelation lineRelationThirdVersion = lineRelationsThirdVersion.stream().iterator().next();
    assertThat(lineRelationThirdVersion).isNotNull();
    assertThat(lineRelationThirdVersion.getSlnid()).isEqualTo("ch:1:ttfnid:111111");
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("BEX2");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.ACTIVE);
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    //new
    TimetableFieldNumberVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
    assertThat(fourthTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(fourthTemporalVersion.getComment()).isEqualTo("Scenario 3");
    Set<TimetableFieldLineRelation> lineRelationsFourthVersion = fourthTemporalVersion.getLineRelations();
    assertThat(lineRelationsFourthVersion).isNotEmpty();
    assertThat(lineRelationsFourthVersion.size()).isEqualTo(1);
    TimetableFieldLineRelation lineRelationFourthVersion = lineRelationsFourthVersion.stream().iterator().next();
    assertThat(lineRelationFourthVersion).isNotNull();
    assertThat(lineRelationFourthVersion.getSlnid()).isEqualTo("ch:1:ttfnid:111111");
    assertThat(fourthTemporalVersion.getNumber()).isEqualTo("BEX3");
    assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.ACTIVE);
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
    assertThat(fifthTemporalVersion.getStatus()).isEqualTo(Status.ACTIVE);
    assertThat(fifthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(fifthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

  }

}
