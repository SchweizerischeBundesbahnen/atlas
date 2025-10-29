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

class TimetableFieldNumberServiceScenario11Test extends BaseTimetableFieldNumberServiceTest {

  @Autowired
  TimetableFieldNumberServiceScenario11Test(
      TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberService timetableFieldNumberService) {
    super(versionRepository, timetableFieldNumberService);
  }

  /**
   * Szenario 11a: Update über mehrere Versionen über die Grenze
   * <p>
   * Änderung   |___________________________________|
   * |-----------|------------|
   * Version 1   Version 2
   * <p>
   * Ergebnis: Versionen an der Grenze werden verlängert
   */
  @Test
  void scenario11a() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    TimetableFieldNumberVersion editedVersion = version1Builder().build();
    editedVersion.setValidFrom(LocalDate.of(2019, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
    editedVersion.setDescriptionOutwardLine1("FPFN Description <changed>");
    editedVersion.setVersion(version1.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version1, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).hasSize(2);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // first version updated
    assertThat(result.getFirst()).isNotNull();
    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2019, 6, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // second version updated
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
    assertThat(secondTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
  }

  /**
   * Szenario 11b: Update über mehrere Versionen über die Grenze
   * <p>
   * Änderung   |_________________________________________________|
   * |-----------|------------|------------|
   * Version 1   Version 2    Version 3
   * <p>
   * Ergebnis: Versionen an der Grenze werden verlängert in der mitte nur properties updated
   */
  @Test
  void scenario11b() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = version1Builder().build();
    editedVersion.setValidFrom(LocalDate.of(2019, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2025, 6, 1));
    editedVersion.setDescriptionOutwardLine1("FPFN Description <changed>");
    editedVersion.setVersion(version1.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version1, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).hasSize(3);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // first version updated
    assertThat(result.getFirst()).isNotNull();
    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2019, 6, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // second version updated
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // third version updated
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 6, 1));
    assertThat(thirdTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(thirdTemporalVersion.getLineRelations()).isEmpty();
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("80.099.3");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
  }

  /**
   * Szenario 11c: Update über mehrere Versionen über die linke Grenze
   * <p>
   * Änderung   |__________________________________________|
   * |-----------|------------|------------|
   * Version 1   Version 2    Version 3
   * <p>
   * Ergebnis: Versionen an der Grenze werden verlängert in der mitte nur properties updated
   */
  @Test
  void scenario11c() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = version1Builder().build();
    editedVersion.setValidFrom(LocalDate.of(2019, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2024, 12, 31));
    editedVersion.setDescriptionOutwardLine1("FPFN Description <changed>");
    editedVersion.setVersion(version1.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version1, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).hasSize(3);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // first version updated
    assertThat(result.getFirst()).isNotNull();
    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2019, 6, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // second version updated
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // third version updated
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(thirdTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(thirdTemporalVersion.getLineRelations()).isEmpty();
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("80.099.3");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
  }

  /**
   * Szenario 11d: Update über mehrere Versionen über die rechte Grenze
   * <p>
   * Änderung        |__________________________________________|
   * |-----------|------------|------------|
   * Version 1   Version 2    Version 3
   * <p>
   * Ergebnis: Versionen an der Grenze werden verlängert in der mitte nur properties updated
   */
  @Test
  void scenario11d() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = version1Builder().build();
    editedVersion.setValidFrom(LocalDate.of(2020, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2025, 6, 1));
    editedVersion.setDescriptionOutwardLine1("FPFN Description <changed>");
    editedVersion.setVersion(version1.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version1, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).hasSize(3);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // first version updated
    assertThat(result.getFirst()).isNotNull();
    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // second version updated
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // third version updated
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 6, 1));
    assertThat(thirdTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("80.099.3");
    assertThat(thirdTemporalVersion.getLineRelations()).isEmpty();
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("80.099.3");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
  }

}
