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

public class VersionServiceScenario5Test extends BaseVersionServiceTest {

  @Autowired
  public VersionServiceScenario5Test(
      VersionRepository versionRepository,
      VersionService versionService) {
    super(versionRepository, versionService);
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
  public void scenario5() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    version4 = versionRepository.save(version4);
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setComment("Scenario 5");
    editedVersion.setValidFrom(LocalDate.of(2020, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2025, 6, 1));
    editedVersion.getLineRelations()
                 .add(LineRelation.builder().slnid("ch:1:fpfnid:111111").version(version3).build());

    //when
    versionService.updateVersion(version3, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(6);
    result.sort(Comparator.comparing(Version::getValidFrom));

    // first current index updated
    assertThat(result.get(0)).isNotNull();
    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 5, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();

    // new
    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 6, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(secondTemporalVersion.getComment()).isEqualTo("Scenario 5");
    assertThat(secondTemporalVersion.getLineRelations()).isNotEmpty();
    Set<LineRelation> lineRelationsSecondVersion = secondTemporalVersion.getLineRelations();
    assertThat(lineRelationsSecondVersion).isNotEmpty();
    assertThat(lineRelationsSecondVersion.size()).isEqualTo(1);
    LineRelation lineRelationSecondVersion = lineRelationsSecondVersion.stream().iterator().next();
    assertThat(lineRelationSecondVersion).isNotNull();
    assertThat(lineRelationSecondVersion.getSlnid()).isEqualTo("ch:1:fpfnid:111111");

    //update
    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(thirdTemporalVersion.getComment()).isEqualTo("Scenario 5");
    Set<LineRelation> lineRelationsThirdVersion = thirdTemporalVersion.getLineRelations();
    assertThat(lineRelationsThirdVersion).isNotEmpty();
    assertThat(lineRelationsThirdVersion.size()).isEqualTo(1);
    LineRelation lineRelationThirdVersion = lineRelationsThirdVersion.stream().iterator().next();
    assertThat(lineRelationThirdVersion).isNotNull();
    assertThat(lineRelationThirdVersion.getSlnid()).isEqualTo("ch:1:fpfnid:111111");

    //new
    Version fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fourthTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(fourthTemporalVersion.getComment()).isEqualTo("Scenario 5");
    Set<LineRelation> lineRelationsFourthVersion = fourthTemporalVersion.getLineRelations();
    assertThat(lineRelationsFourthVersion).isNotEmpty();
    assertThat(lineRelationsFourthVersion.size()).isEqualTo(1);
    LineRelation lineRelationFourthVersion = lineRelationsFourthVersion.stream().iterator().next();
    assertThat(lineRelationFourthVersion).isNotNull();
    assertThat(lineRelationFourthVersion.getSlnid()).isEqualTo("ch:1:fpfnid:111111");

    //new
    Version fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 6, 1));
    assertThat(fifthTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(fifthTemporalVersion.getComment()).isEqualTo("Scenario 5");
    Set<LineRelation> lineRelationsFifthVersion = fifthTemporalVersion.getLineRelations();
    assertThat(lineRelationsFifthVersion).isNotEmpty();
    assertThat(lineRelationsFifthVersion.size()).isEqualTo(1);
    LineRelation lineRelationFifthVersion = lineRelationsFifthVersion.stream().iterator().next();
    assertThat(lineRelationFifthVersion).isNotNull();
    assertThat(lineRelationFifthVersion.getSlnid()).isEqualTo("ch:1:fpfnid:111111");

    //last current index updated
    Version sixthTemporalVersion = result.get(5);
    assertThat(sixthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 6, 2));
    assertThat(sixthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(sixthTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(sixthTemporalVersion.getComment()).isNull();
    assertThat(sixthTemporalVersion.getLineRelations()).isEmpty();

  }

}