package ch.sbb.timetable.field.number.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.repository.VersionRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class VersionServiceScenario10Test extends BaseVersionServiceTest {

  @Autowired
  public VersionServiceScenario10Test(
      VersionRepository versionRepository,
      VersionService versionService) {
    super(versionRepository, versionService);
  }

  /**
   * Szenario 10a (Spezialfall 3): Update in der Lücke zwischen zwei Versionen
   *
   * Änderung                     |_________|
   *                 |-----------|           |-------------|
   *                   Version 1                Version 2
   *
   * Ergebnis: Neue Version wird erstellt (mit Inhalt von Änderung und Version 3)
   */
  @Test
  public void scenario10a() {
    //given
    version1 = versionRepository.save(version1);
    version3 = versionRepository.save(version3);
    Version editedVersion = new Version();
    editedVersion.setValidFrom(LocalDate.of(2022, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2023, 12, 31));
    editedVersion.setName("FPFN Name <changed>");

    //when
    versionService.updateVersion(version1, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(3);
    result.sort(Comparator.comparing(Version::getValidFrom));

    // first version no changes
    assertThat(result.get(0)).isNotNull();
    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("BEX1");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();

    // second version new version added
    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();

    // second version new version added
    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(thirdTemporalVersion.getLineRelations()).isEmpty();

  }
  /**
   * Szenario 10a (Spezialfall 3): Update in der Lücke zwischen zwei Versionen
   *
   * Änderung                     |_________|
   *                 |-----------|           |-------------|
   *                   Version 1                Version 2
   *
   * Ergebnis: Neue Version wird erstellt (mit Inhalt von Änderung und Version 3)
   */
  @Test
  public void scenario10aMergeWhenNoPropertiesChanged() {
    //given
    version1 = versionRepository.save(version1);
    version3 = versionRepository.save(version3);
    Version editedVersion = new Version();
    editedVersion.setValidFrom(LocalDate.of(2022, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2023, 12, 31));

    //when
    versionService.updateVersion(version1, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
    result.sort(Comparator.comparing(Version::getValidFrom));

    // first version updated
    assertThat(result.get(0)).isNotNull();
    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("BEX1");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();

    // second version not touched
    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();

  }

  /**
   * Szenario 10b (Spezialfall 6): Update, welches über die Lücke zwischen zwei Versionen hinaus geht
   *
   * Änderung                 |_________________|
   *                 |------------|         |-------------|
   *                   Version 1               Version 2
   *
   * Ergebnis: In der Lücke wird eine neue Version erstellt (mit Inhalt von Änderung und Version 3)
   */
  @Test
  public void scenario10b() {
    //given
    version1 = versionRepository.save(version1);
    version3 = versionRepository.save(version3);
    Version editedVersion = new Version();
    editedVersion.setValidFrom(LocalDate.of(2020, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
    editedVersion.setName("FPFN Name <changed>");

    //when
    versionService.updateVersion(version1, editedVersion);
    List<Version> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(4);
    result.sort(Comparator.comparing(Version::getValidFrom));

    // first version no changes
    assertThat(result.get(0)).isNotNull();
    Version firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 5, 31));
    assertThat(firstTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("BEX1");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();

    // second version new version added
    Version secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 6, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("BEX1");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();

    // third version new version added
    Version thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
    assertThat(thirdTemporalVersion.getName()).isEqualTo("FPFN Name <changed>");
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("BEX3");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(thirdTemporalVersion.getLineRelations()).isEmpty();

    // fourth version new version added
    Version fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 6, 2));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fourthTemporalVersion.getName()).isEqualTo("FPFN Name");
    assertThat(fourthTemporalVersion.getNumber()).isEqualTo("BEX3");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(fourthTemporalVersion.getLineRelations()).isEmpty();


  }

}