package ch.sbb.timetable.field.number.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.entity.LineRelation;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.repository.VersionRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class VersionServiceScenario1Test extends BaseVersionServiceTest {

  @Autowired
  public VersionServiceScenario1Test(
      VersionRepository versionRepository,
      VersionService versionService) {
    super(versionRepository, versionService);
  }

  /**
   * Szenario 1a: Update einer bestehenden Version am Ende
   * NEU:                             |________________________________
   * IST:      |----------------------|--------------------------------
   * Version:        1                                2
   *
   * RESULTAT: |----------------------|________________________________
   * Version:        1                                2
   */
  @Test
  public void scenario1a() {
    //given
    version2.setLineRelations(new HashSet<>(
        Set.of(LineRelation.builder().slnid(TTFNID).version(version2).build(),
            LineRelation.builder().slnid(TTFNID).version(version2).build())));
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);

    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <CHANGED>");
    editedVersion.getLineRelations()
                 .add(LineRelation.builder().slnid("ch:1:fpfnid:111111").version(version2).build());
    //when
    versionService.updateVersion(version2, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version2.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
    result.sort(Comparator.comparing(Version::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name");

    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name <CHANGED>");
    assertThat(secondTemporalVersion.getLineRelations()).isNotEmpty();
    assertThat(secondTemporalVersion.getLineRelations().size()).isEqualTo(1);
    Set<LineRelation> lineRelations = secondTemporalVersion.getLineRelations();
    LineRelation lineRelation = lineRelations.stream().iterator().next();
    assertThat(lineRelation).isNotNull();
    assertThat(lineRelation.getSlnid()).isEqualTo("ch:1:fpfnid:111111");
  }

  /**
   * Szenario 1a: Update einer bestehenden Version am Ende
   * NEU:                             |________________________________
   * IST:      |----------------------|--------------------------------
   * Version:        1                                2
   *
   * RESULTAT: |----------------------|________________________________
   * Version:        1                                2
   */
  @Test
  public void scenario1aEditedValidFromAndEditedValidToAreEqualsToCurrentValidFromAndCurrentValidTo() {
    //given
    version2.setLineRelations(new HashSet<>(
        Set.of(LineRelation.builder().slnid(TTFNID).version(version2).build(),
            LineRelation.builder().slnid(TTFNID).version(version2).build())));
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);

    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <CHANGED>");
    editedVersion.setValidFrom(version2.getValidFrom());
    editedVersion.setValidTo(version2.getValidTo());
    editedVersion.getLineRelations()
                 .add(LineRelation.builder().slnid("ch:1:fpfnid:111111").version(version2).build());
    //when
    versionService.updateVersion(version2, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version2.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
    result.sort(Comparator.comparing(Version::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name");

    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name <CHANGED>");
    assertThat(secondTemporalVersion.getLineRelations()).isNotEmpty();
    assertThat(secondTemporalVersion.getLineRelations().size()).isEqualTo(1);
    Set<LineRelation> lineRelations = secondTemporalVersion.getLineRelations();
    LineRelation lineRelation = lineRelations.stream().iterator().next();
    assertThat(lineRelation).isNotNull();
    assertThat(lineRelation.getSlnid()).isEqualTo("ch:1:fpfnid:111111");
  }

  /**
   * Szenario 1b: Update einer bestehenden Version in der Mitte
   * NEU:                  |______________________|
   * IST:      |-----------|----------------------|--------------------
   * Version:        1                 2                  3
   *
   * RESULTAT: |-----------|______________________|--------------------
   * Version:        1                 2                  3
   */
  @Test
  public void scenario1b() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);

    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");

    //when
    versionService.updateVersion(version2, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version2.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(3);
    result.sort(Comparator.comparing(Version::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name");

    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");

    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name");
  }

  /**
   * Szenario 1c: Update einer bestehenden Version am Anfang
   *
   * NEU:       |___________|
   * IST:       |-----------|----------------------|--------------------
   * Version:         1                 2                   3
   *
   * RESULTAT: |___________|----------------------|--------------------
   * Version:        1                 2                  3
   */
  @Test
  public void scenario1c() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);

    Version editedVersion = new Version();
    editedVersion.setName("FPFN Name <changed>");
    editedVersion.setComment("A comment");

    //when
    versionService.updateVersion(version1, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(3);
    result.sort(Comparator.comparing(Version::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(firstTemporalVersion.getComment()).isEqualTo("A comment");

    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(secondTemporalVersion.getComment()).isNull();

    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(thirdTemporalVersion.getComment()).isNull();
  }

  /**
   * Szenario 1d
   *
   * NEU:                 |______________________|
   * IST:      |----------|----------|----------|----------|----------|
   * Version:        1          2          3          4         5
   *
   * RESULTAT: |----------|----------|----------|----------|----------|
   * Version:        1          2          3          4         5         version 2 und 3 werden nur UPDATED
   */
  @Test
  public void scenario1d() {
    //given
    version1.setName("SBB1");
    version1 = versionRepository.save(version1);
    version2.setName("SBB2");
    version2 = versionRepository.save(version2);
    version3.setName("SBB3");
    version3 = versionRepository.save(version3);
    version4.setName("SBB1");
    version4 = versionRepository.save(version4);
    version5.setName("SBB4");
    version5 = versionRepository.save(version5);
    Version editedVersion = new Version();
    editedVersion.setName("SBB1");
    editedVersion.setValidFrom(version2.getValidFrom());
    editedVersion.setValidTo(version3.getValidTo());

    //when
    versionService.updateVersion(version2, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(5);
    result.sort(Comparator.comparing(Version::getValidFrom));

    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("SBB1");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();

    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("SBB1");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();

    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("SBB1");
    assertThat(thirdTemporalVersion.getComment()).isNull();
    assertThat(thirdTemporalVersion.getLineRelations()).isEmpty();

    Version fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(fourthTemporalVersion.getName()).isEqualTo("SBB1");
    assertThat(fourthTemporalVersion.getComment()).isNull();
    assertThat(fourthTemporalVersion.getLineRelations()).isEmpty();

    Version fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2026, 1, 1));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2026, 12, 31));
    assertThat(fifthTemporalVersion.getName()).isEqualTo("SBB4");
    assertThat(fifthTemporalVersion.getComment()).isNull();
    assertThat(fifthTemporalVersion.getLineRelations()).isEmpty();

  }

}