package ch.sbb.line.directory.module.ttfn.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.module.ttfn.repository.TimetableFieldNumberVersionRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TimetableFieldNumberServiceScenario9Test extends BaseTimetableFieldNumberServiceTest {

  @Autowired
  TimetableFieldNumberServiceScenario9Test(
      TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberService timetableFieldNumberService) {
    super(versionRepository, timetableFieldNumberService);
  }

  /**
   * Szenario 9a (Fall 5): Update ausserhalb der existierenden version
   * <p>
   * Änderung 1     |_____|
   * Version                 |--------------------|
   * <p>
   * Ergebnis                |--------------------|
   * Version ist vom update nicht betroffen
   */
  @Test
  void scenario9a() {
    //given
    version1 = versionRepository.save(version1);
    TimetableFieldNumberVersion editedVersion = version1Builder().build();
    editedVersion.setValidFrom(LocalDate.of(2019, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2019, 6, 1));
    editedVersion.setDescriptionOutwardLine1("FPFN Description <changed>");
    editedVersion.setVersion(version1.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version1, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).hasSize(2);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // new version
    assertThat(result.getFirst()).isNotNull();
    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2019, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2019, 6, 1));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // second version no changes
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
  }

  /**
   * Szenario 9b (Spezialfall 5): Update vor erster existierender Version
   * <p>
   * Änderung  |___|
   * |-----------------|----------------|-----------|         |-------------|
   * Version 1          Version 2     Version 3               Version 4
   * <p>
   * Ergebnis: Neue Version wird erstellt (mit Inhalt von Änderung und Version 1)
   */
  @Test
  void scenario9b() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    TimetableFieldNumberVersion editedVersion = version1Builder().build();
    editedVersion.setValidFrom(LocalDate.of(2019, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2019, 6, 1));
    editedVersion.setDescriptionOutwardLine1("FPFN Description <changed>");
    editedVersion.setVersion(version1.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version1, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).hasSize(3);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // new version
    assertThat(result.getFirst()).isNotNull();
    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2019, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2019, 6, 1));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // second version no changes
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // third version no changes
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(thirdTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(thirdTemporalVersion.getLineRelations()).isEmpty();
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
  }

  /**
   * Szenario 9c (Spezialfall 1): Update vor und während erster Version
   * Änderung    |_____________________|
   * |-----------------|----------------|-----------|         |-------------|
   * Version 1          Version 2     Version 3               Version 4
   * <p>
   * Ergebnis: Version 1 wird verlängert
   */
  @Test
  void scenario9c() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    TimetableFieldNumberVersion editedVersion = version1Builder().build();
    editedVersion.setValidFrom(LocalDate.of(2019, 1, 1));
    editedVersion.setValidTo(version1.getValidTo());
    editedVersion.setDescriptionOutwardLine1("FPFN Description <changed>");
    editedVersion.setVersion(version1.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version1, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).hasSize(2);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // version update
    assertThat(result.getFirst()).isNotNull();
    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2019, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // second version no changes
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
  }

}
