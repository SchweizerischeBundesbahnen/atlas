package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

 class TimetableFieldNumberServiceScenario10Test extends BaseTimetableFieldNumberServiceTest {

  @Autowired
   TimetableFieldNumberServiceScenario10Test(
      TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberService timetableFieldNumberService) {
    super(versionRepository, timetableFieldNumberService);
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
   void scenario10a() {
    //given
    version1 = versionRepository.save(version1);
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = version1Builder().build();
    editedVersion.setValidFrom(LocalDate.of(2022, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2023, 12, 31));
    editedVersion.setDescription("FPFN Description <changed>");

    //when
    timetableFieldNumberService.updateVersion(version1, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).hasSize(3);
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

    // second version new version added
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("BEX1");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // second version new version added
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(thirdTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(thirdTemporalVersion.getComment()).isNull();
    assertThat(thirdTemporalVersion.getLineRelations()).isEmpty();
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("BEX3");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

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
   void scenario10aMergeWhenNoPropertiesChanged() {
    //given
    version1 = versionRepository.save(version1);
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = version1Builder().build();
    editedVersion.setValidFrom(LocalDate.of(2022, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2023, 12, 31));

    //when
    timetableFieldNumberService.updateVersion(version1, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).hasSize(2);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // first version updated
    assertThat(result.get(0)).isNotNull();
    TimetableFieldNumberVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(firstTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("BEX1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // second version not touched
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("BEX3");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

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
   void scenario10b() {
    //given
    version1 = versionRepository.save(version1);
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = version1Builder().build();
    editedVersion.setValidFrom(LocalDate.of(2020, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
    editedVersion.setDescription("FPFN Description <changed>");

    //when
    timetableFieldNumberService.updateVersion(version1, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).hasSize(4);
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

    // second version new version added
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 6, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("BEX1");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // third version new version added
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
    assertThat(thirdTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(thirdTemporalVersion.getComment()).isNull();
    assertThat(thirdTemporalVersion.getLineRelations()).isEmpty();
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("BEX3");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // fourth version new version added
    TimetableFieldNumberVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 6, 2));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fourthTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(fourthTemporalVersion.getComment()).isNull();
    assertThat(fourthTemporalVersion.getLineRelations()).isEmpty();
    assertThat(fourthTemporalVersion.getNumber()).isEqualTo("BEX3");
    assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fourthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

  }

}
