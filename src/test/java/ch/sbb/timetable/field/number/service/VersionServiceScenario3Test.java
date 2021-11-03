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

public class VersionServiceScenario3Test extends BaseVersionServiceTest {

  @Autowired
  public VersionServiceScenario3Test(
      VersionRepository versionRepository,
      VersionService versionService) {
    super(versionRepository, versionService);
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
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setComment("Scenario 3");
    editedVersion.setValidFrom(LocalDate.of(2023, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
    editedVersion.getLineRelations()
                 .add(LineRelation.builder().slnid("ch:1:fpfnid:111111").version(version2).build());

    //when
    versionService.updateVersion(version2, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(5);
    result.sort(Comparator.comparing(Version::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    // not touched
    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("SBB1");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();

    // first current index updated
    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 5, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("SBB2");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();

    //new
    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2023, 6, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(thirdTemporalVersion.getComment()).isEqualTo("Scenario 3");
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("SBB2");
    Set<LineRelation> lineRelationsThirdVersion = thirdTemporalVersion.getLineRelations();
    assertThat(lineRelationsThirdVersion).isNotEmpty();
    assertThat(lineRelationsThirdVersion.size()).isEqualTo(1);
    LineRelation lineRelationThirdVersion = lineRelationsThirdVersion.stream().iterator().next();
    assertThat(lineRelationThirdVersion).isNotNull();
    assertThat(lineRelationThirdVersion.getSlnid()).isEqualTo("ch:1:fpfnid:111111");

    //new
    Version fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
    assertThat(fourthTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(fourthTemporalVersion.getComment()).isEqualTo("Scenario 3");
    assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("SBB3");
    Set<LineRelation> lineRelationsFourthVersion = fourthTemporalVersion.getLineRelations();
    assertThat(lineRelationsFourthVersion).isNotEmpty();
    assertThat(lineRelationsFourthVersion.size()).isEqualTo(1);
    LineRelation lineRelationFourthVersion = lineRelationsFourthVersion.stream().iterator().next();
    assertThat(lineRelationFourthVersion).isNotNull();
    assertThat(lineRelationFourthVersion.getSlnid()).isEqualTo("ch:1:fpfnid:111111");

    //last current index updated
    Version fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 6, 2));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fifthTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(fifthTemporalVersion.getBusinessOrganisation()).isEqualTo("SBB3");
    assertThat(fifthTemporalVersion.getComment()).isNull();
    assertThat(fifthTemporalVersion.getLineRelations()).isEmpty();

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
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setComment("Scenario 3");
    editedVersion.setValidFrom(LocalDate.of(2023, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
    editedVersion.getLineRelations()
                 .add(LineRelation.builder().slnid("ch:1:fpfnid:111111").version(version3).build());

    //when
    versionService.updateVersion(version3, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(5);
    result.sort(Comparator.comparing(Version::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    // not touched
    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();

    // first current index updated
    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 5, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();

    //new
    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2023, 6, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(thirdTemporalVersion.getComment()).isEqualTo("Scenario 3");
    Set<LineRelation> lineRelationsThirdVersion = thirdTemporalVersion.getLineRelations();
    assertThat(lineRelationsThirdVersion).isNotEmpty();
    assertThat(lineRelationsThirdVersion.size()).isEqualTo(1);
    LineRelation lineRelationThirdVersion = lineRelationsThirdVersion.stream().iterator().next();
    assertThat(lineRelationThirdVersion).isNotNull();
    assertThat(lineRelationThirdVersion.getSlnid()).isEqualTo("ch:1:fpfnid:111111");

    //new
    Version fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
    assertThat(fourthTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(fourthTemporalVersion.getComment()).isEqualTo("Scenario 3");
    Set<LineRelation> lineRelationsFourthVersion = fourthTemporalVersion.getLineRelations();
    assertThat(lineRelationsFourthVersion).isNotEmpty();
    assertThat(lineRelationsFourthVersion.size()).isEqualTo(1);
    LineRelation lineRelationFourthVersion = lineRelationsFourthVersion.stream().iterator().next();
    assertThat(lineRelationFourthVersion).isNotNull();
    assertThat(lineRelationFourthVersion.getSlnid()).isEqualTo("ch:1:fpfnid:111111");

    //last current index updated
    Version fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 6, 2));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fifthTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(fifthTemporalVersion.getComment()).isNull();
    assertThat(fifthTemporalVersion.getLineRelations()).isEmpty();

  }

}