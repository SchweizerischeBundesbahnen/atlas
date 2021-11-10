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

public class VersionServiceScenario7Test extends BaseVersionServiceTest {

  @Autowired
  public VersionServiceScenario7Test(
      VersionRepository versionRepository,
      VersionService versionService) {
    super(versionRepository, versionService);
  }

  /**
   * Szenario 7a: Neue Version in der Zukunft, die letzte Version nur berührt
   *
   * NEU:                       |________________________________
   * IST:      |----------------|
   * Version:           1
   *
   * RESULTAT: |----------------|________________________________     NEUE VERSION EINGEFÜGT
   * Version:          1                         2
   */
  @Test
  public void scenario7a() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setComment("Scenario 7a");
    editedVersion.setValidFrom(LocalDate.of(2025, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2025, 12, 31));
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

    // third version no changes
    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name");
    Set<LineRelation> lineRelationsThirdVersion = thirdTemporalVersion.getLineRelations();
    assertThat(lineRelationsThirdVersion).isEmpty();

    //fourth new
    Version fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(fourthTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(fourthTemporalVersion.getComment()).isEqualTo("Scenario 7a");
    Set<LineRelation> lineRelationsFourthVersion = fourthTemporalVersion.getLineRelations();
    assertThat(lineRelationsFourthVersion).isNotEmpty();
    assertThat(lineRelationsFourthVersion.size()).isEqualTo(1);
    LineRelation lineRelationFourthVersion = lineRelationsFourthVersion.stream().iterator().next();
    assertThat(lineRelationFourthVersion).isNotNull();
    assertThat(lineRelationFourthVersion.getSlnid()).isEqualTo("ch:1:fpfnid:111111");

  }

  /**
   * Szenario 7b: Neue Version in der Vergangenheit, die nächste Version nur berührt
   *
   * NEU:      |________________________________|
   * IST:                                       |----------------|
   * Version:                                           1
   *
   * RESULTAT: |________________________________|----------------|     NEUE VERSION EINGEFÜGT
   * Version:                 2                         1
   */
  @Test
  public void scenario7b() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setComment("Scenario 7b");
    editedVersion.setValidFrom(LocalDate.of(2019, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2019, 12, 31));
    editedVersion.getLineRelations()
                 .add(LineRelation.builder().slnid("ch:1:fpfnid:111111").version(version1).build());

    //when
    versionService.updateVersion(version1, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(4);
    result.sort(Comparator.comparing(Version::getValidFrom));

    //first new
    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2019, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2019, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(firstTemporalVersion.getComment()).isEqualTo("Scenario 7b");
    Set<LineRelation> lineRelationsFirstVersion = firstTemporalVersion.getLineRelations();
    assertThat(lineRelationsFirstVersion).isNotEmpty();
    assertThat(lineRelationsFirstVersion.size()).isEqualTo(1);
    LineRelation lineRelationFirstVersion = lineRelationsFirstVersion.stream().iterator().next();
    assertThat(lineRelationFirstVersion).isNotNull();
    assertThat(lineRelationFirstVersion.getSlnid()).isEqualTo("ch:1:fpfnid:111111");


    // first version no changes
    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();

    // second version no changes
    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(thirdTemporalVersion.getComment()).isNull();
    assertThat(thirdTemporalVersion.getLineRelations()).isEmpty();

    // third version no changes
    Version fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fourthTemporalVersion.getName()).isEqualTo("FPFN Name");
    Set<LineRelation> lineRelationsFourthVersion = fourthTemporalVersion.getLineRelations();
    assertThat(lineRelationsFourthVersion).isEmpty();

  }

  /**
   * Szenario 7c: Neue Version in der Zukunft, die letzte Version nicht überschneidet
   *
   * NEU:                             |________________________________
   * IST:      |----------------|
   * Version:           1
   *
   * RESULTAT: |----------------|     |________________________________     NEUE VERSION EINGEFÜGT
   * Version:          1                               2
   */
  @Test
  public void scenario7c() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setComment("Scenario 7c");
    editedVersion.setValidFrom(LocalDate.of(2025, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2025, 12, 31));
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

    // third version no changes
    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name");
    Set<LineRelation> lineRelationsThirdVersion = thirdTemporalVersion.getLineRelations();
    assertThat(lineRelationsThirdVersion).isEmpty();

    //fourth new
    Version fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 6, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(fourthTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(fourthTemporalVersion.getComment()).isEqualTo("Scenario 7c");
    Set<LineRelation> lineRelationsFourthVersion = fourthTemporalVersion.getLineRelations();
    assertThat(lineRelationsFourthVersion).isNotEmpty();
    assertThat(lineRelationsFourthVersion.size()).isEqualTo(1);
    LineRelation lineRelationFourthVersion = lineRelationsFourthVersion.stream().iterator().next();
    assertThat(lineRelationFourthVersion).isNotNull();
    assertThat(lineRelationFourthVersion.getSlnid()).isEqualTo("ch:1:fpfnid:111111");

  }

  /**
   * Szenario 7d: Neue Version in der Vergangenheit, die nächste Version nicht überschneidet
   *
   * NEU:      |________________________________|
   * IST:                                            |----------------|
   * Version:                                                1
   *
   * RESULTAT: |________________________________|    |----------------|     NEUE VERSION EINGEFÜGT
   * Version:                 2                              1
   */
  @Test
  public void scenario7d() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setComment("Scenario 7d");
    editedVersion.setValidFrom(LocalDate.of(2019, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2019, 5, 31));
    editedVersion.getLineRelations()
                 .add(LineRelation.builder().slnid("ch:1:fpfnid:111111").version(version1).build());

    //when
    versionService.updateVersion(version1, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(4);
    result.sort(Comparator.comparing(Version::getValidFrom));

    //first new
    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2019, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2019, 5, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(firstTemporalVersion.getComment()).isEqualTo("Scenario 7d");
    Set<LineRelation> lineRelationsFirstVersion = firstTemporalVersion.getLineRelations();
    assertThat(lineRelationsFirstVersion).isNotEmpty();
    assertThat(lineRelationsFirstVersion.size()).isEqualTo(1);
    LineRelation lineRelationFirstVersion = lineRelationsFirstVersion.stream().iterator().next();
    assertThat(lineRelationFirstVersion).isNotNull();
    assertThat(lineRelationFirstVersion.getSlnid()).isEqualTo("ch:1:fpfnid:111111");


    // first version no changes
    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();

    // second version no changes
    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(thirdTemporalVersion.getComment()).isNull();
    assertThat(thirdTemporalVersion.getLineRelations()).isEmpty();

    // third version no changes
    Version fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fourthTemporalVersion.getName()).isEqualTo("FPFN Name");
    Set<LineRelation> lineRelationsFourthVersion = fourthTemporalVersion.getLineRelations();
    assertThat(lineRelationsFourthVersion).isEmpty();

  }

}