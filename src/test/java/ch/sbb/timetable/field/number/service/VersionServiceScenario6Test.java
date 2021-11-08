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

public class VersionServiceScenario6Test extends BaseVersionServiceTest {

  @Autowired
  public VersionServiceScenario6Test(
      VersionRepository versionRepository,
      VersionService versionService) {
    super(versionRepository, versionService);
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
  public void scenario6() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setComment("Scenario 6");
    editedVersion.setValidFrom(LocalDate.of(2024, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2024, 12, 31));
    editedVersion.getLineRelations()
                 .add(LineRelation.builder().slnid("ch:1:fpfnid:111111").version(version3).build());

    //when
    versionService.updateVersion(version3, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(4);
    result.sort(Comparator.comparing(Version::getValidFrom));

    // first version no changes
    assertThat(result.get(0)).isNotNull();
    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();

    // second version no changes
    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();

    // third version update
    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 5, 31));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name");
    Set<LineRelation> lineRelationsThirdVersion = thirdTemporalVersion.getLineRelations();
    assertThat(lineRelationsThirdVersion).isEmpty();

    //fourth new
    Version fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 6, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fourthTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(fourthTemporalVersion.getComment()).isEqualTo("Scenario 6");
    Set<LineRelation> lineRelationsFourthVersion = fourthTemporalVersion.getLineRelations();
    assertThat(lineRelationsFourthVersion).isNotEmpty();
    assertThat(lineRelationsFourthVersion.size()).isEqualTo(1);
    LineRelation lineRelationFourthVersion = lineRelationsFourthVersion.stream().iterator().next();
    assertThat(lineRelationFourthVersion).isNotNull();
    assertThat(lineRelationFourthVersion.getSlnid()).isEqualTo("ch:1:fpfnid:111111");

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
  public void scenario6WithOnlyOneVersion() {
    //given
    version1 = versionRepository.save(version1);
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setComment("Scenario 6");
    editedVersion.setValidFrom(LocalDate.of(2020, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2021, 12, 31));
    editedVersion.getLineRelations()
                 .add(LineRelation.builder().slnid("ch:1:fpfnid:111111").version(version1).build());

    //when
    versionService.updateVersion(version1, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
    result.sort(Comparator.comparing(Version::getValidFrom));

    // first version no changes
    assertThat(result.get(0)).isNotNull();
    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 5, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();

    //second new
    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 6, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(secondTemporalVersion.getComment()).isEqualTo("Scenario 6");
    Set<LineRelation> lineRelationsSecondVersion = secondTemporalVersion.getLineRelations();
    assertThat(lineRelationsSecondVersion).isNotEmpty();
    assertThat(lineRelationsSecondVersion.size()).isEqualTo(1);
    LineRelation lineRelationSecondVersion = lineRelationsSecondVersion.stream().iterator().next();
    assertThat(lineRelationSecondVersion).isNotNull();
    assertThat(lineRelationSecondVersion.getSlnid()).isEqualTo("ch:1:fpfnid:111111");

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
  public void scenario6WhenEditedValidToIsBiggerThenCurrentValidTo() {
    //given
    version1 = versionRepository.save(version1);
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setComment("Scenario 6");
    editedVersion.setValidFrom(LocalDate.of(2020, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2022, 12, 31));
    editedVersion.getLineRelations()
                 .add(LineRelation.builder().slnid("ch:1:fpfnid:111111").version(version1).build());

    //when
    versionService.updateVersion(version1, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
    result.sort(Comparator.comparing(Version::getValidFrom));

    // first version no changes
    assertThat(result.get(0)).isNotNull();
    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 5, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();

    //second new
    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 6, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2022, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(secondTemporalVersion.getComment()).isEqualTo("Scenario 6");
    Set<LineRelation> lineRelationsSecondVersion = secondTemporalVersion.getLineRelations();
    assertThat(lineRelationsSecondVersion).isNotEmpty();
    assertThat(lineRelationsSecondVersion.size()).isEqualTo(1);
    LineRelation lineRelationSecondVersion = lineRelationsSecondVersion.stream().iterator().next();
    assertThat(lineRelationSecondVersion).isNotNull();
    assertThat(lineRelationSecondVersion.getSlnid()).isEqualTo("ch:1:fpfnid:111111");

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
  public void scenario6WhenOnlyValidFromIsEdited() {
    //given
    version1 = versionRepository.save(version1);
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setComment("Scenario 6");
    editedVersion.setValidFrom(LocalDate.of(2020, 6, 1));
    editedVersion.getLineRelations()
                 .add(LineRelation.builder().slnid("ch:1:fpfnid:111111").version(version1).build());

    //when
    versionService.updateVersion(version1, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
    result.sort(Comparator.comparing(Version::getValidFrom));

    // first version no changes
    assertThat(result.get(0)).isNotNull();
    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 5, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();

    //second new
    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 6, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(secondTemporalVersion.getComment()).isEqualTo("Scenario 6");
    Set<LineRelation> lineRelationsSecondVersion = secondTemporalVersion.getLineRelations();
    assertThat(lineRelationsSecondVersion).isNotEmpty();
    assertThat(lineRelationsSecondVersion.size()).isEqualTo(1);
    LineRelation lineRelationSecondVersion = lineRelationsSecondVersion.stream().iterator().next();
    assertThat(lineRelationSecondVersion).isNotNull();
    assertThat(lineRelationSecondVersion.getSlnid()).isEqualTo("ch:1:fpfnid:111111");

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
  public void scenario6WhenOnlyValidToIsEdited() {
    //given
    version1 = versionRepository.save(version1);
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setComment("Scenario 6");
    editedVersion.setValidTo(LocalDate.of(2023, 12, 31));
    editedVersion.getLineRelations()
                 .add(LineRelation.builder().slnid("ch:1:fpfnid:111111").version(version1).build());

    //when
    versionService.updateVersion(version1, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(1);
    result.sort(Comparator.comparing(Version::getValidFrom));

    // first version no changes
    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(firstTemporalVersion.getComment()).isEqualTo("Scenario 6");
    Set<LineRelation> lineRelationsFirstVersion = firstTemporalVersion.getLineRelations();
    assertThat(lineRelationsFirstVersion).isNotEmpty();
    assertThat(lineRelationsFirstVersion.size()).isEqualTo(1);
    LineRelation lineRelationFirstVersion = lineRelationsFirstVersion.stream().iterator().next();
    assertThat(lineRelationFirstVersion).isNotNull();
    assertThat(lineRelationFirstVersion.getSlnid()).isEqualTo("ch:1:fpfnid:111111");

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
  public void scenario6WhenOnlyValidToIsEditedasdasdas() {
    //given
    version1 = versionRepository.save(version1);
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setComment("Scenario 6");
    editedVersion.setValidTo(LocalDate.of(2023, 6, 1));
    editedVersion.getLineRelations()
                 .add(LineRelation.builder().slnid("ch:1:fpfnid:111111").version(version1).build());

    //when
    versionService.updateVersion(version1, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(1);
    result.sort(Comparator.comparing(Version::getValidFrom));

    // first version no changes
    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 6, 1));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(firstTemporalVersion.getComment()).isEqualTo("Scenario 6");
    Set<LineRelation> lineRelationsFirstVersion = firstTemporalVersion.getLineRelations();
    assertThat(lineRelationsFirstVersion).isNotEmpty();
    assertThat(lineRelationsFirstVersion.size()).isEqualTo(1);
    LineRelation lineRelationFirstVersion = lineRelationsFirstVersion.stream().iterator().next();
    assertThat(lineRelationFirstVersion).isNotNull();
    assertThat(lineRelationFirstVersion.getSlnid()).isEqualTo("ch:1:fpfnid:111111");

  }

}