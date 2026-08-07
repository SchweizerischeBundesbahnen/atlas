package ch.sbb.line.directory.module.ttfn.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.versioning.exception.DateOrderException;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.module.ttfn.repository.TimetableFieldNumberVersionRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TimetableFieldNumberServiceScenario7Test extends BaseTimetableFieldNumberServiceTest {

  @Autowired
  TimetableFieldNumberServiceScenario7Test(
      TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberService timetableFieldNumberService) {
    super(versionRepository, timetableFieldNumberService);
  }

  /**
   * Szenario 7a: Neue Version in der Zukunft, die letzte Version nur berührt
   * <p>
   * NEU:                       |________________________________
   * IST:      |----------------|
   * Version:           1
   * <p>
   * RESULTAT: |----------------|________________________________     NEUE VERSION EINGEFÜGT
   * Version:          1                         2
   */
  @Test
  void scenario7a() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = version3Builder().build();
    editedVersion.setDescriptionOutwardLine1("FPFN Description <changed>");
    editedVersion.setValidFrom(LocalDate.of(2025, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2025, 12, 31));
    editedVersion.setVersion(version3.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version3, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.findBySid4ptOrderByValidFrom(version1.getTtfnid());

    //then
    assertThat(result).hasSize(4);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // first version no changes
    assertThat(result.getFirst()).isNotNull();
    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // second version no changes
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // third version no changes
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(thirdTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("80.099.3");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    //fourth new
    TimetableFieldNumberVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(fourthTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(fourthTemporalVersion.getNumber()).isEqualTo("80.099.3");
    assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
  }

  /**
   * Szenario 7b: Neue Version in der Vergangenheit, die nächste Version nur berührt
   * <p>
   * NEU:      |________________________________|
   * IST:                                       |----------------|
   * Version:                                           1
   * <p>
   * RESULTAT: |________________________________|----------------|     NEUE VERSION EINGEFÜGT
   * Version:                 2                         1
   */
  @Test
  void scenario7b() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = version1Builder().build();
    editedVersion.setDescriptionOutwardLine1("FPFN Description <changed>");
    editedVersion.setValidFrom(LocalDate.of(2019, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2019, 12, 31));
    editedVersion.setVersion(version1.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version1, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.findBySid4ptOrderByValidFrom(version1.getTtfnid());

    //then
    assertThat(result).hasSize(4);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    //first new
    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2019, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2019, 12, 31));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // first version no changes
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // second version no changes
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(thirdTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // third version no changes
    TimetableFieldNumberVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fourthTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(fourthTemporalVersion.getNumber()).isEqualTo("80.099.3");
    assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
  }

  /**
   * Szenario 7c: Neue Version in der Zukunft, die letzte Version nicht überschneidet
   * <p>
   * NEU:                             |________________________________
   * IST:      |----------------|
   * Version:           1
   * <p>
   * RESULTAT: |----------------|     |________________________________     NEUE VERSION EINGEFÜGT
   * Version:          1                               2
   */
  @Test
  void scenario7c() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = version3Builder().build();
    editedVersion.setDescriptionOutwardLine1("FPFN Description <changed>");
    editedVersion.setValidFrom(LocalDate.of(2025, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2025, 12, 31));
    editedVersion.setVersion(version3.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version3, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.findBySid4ptOrderByValidFrom(version1.getTtfnid());

    //then
    assertThat(result).hasSize(4);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // first version no changes
    assertThat(result.getFirst()).isNotNull();
    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // second version no changes
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // third version no changes
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(thirdTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("80.099.3");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    //fourth new
    TimetableFieldNumberVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 6, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(fourthTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(fourthTemporalVersion.getNumber()).isEqualTo("80.099.3");
    assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
  }

  /**
   * Szenario 7d: Neue Version in der Vergangenheit, die nächste Version nicht überschneidet
   * <p>
   * NEU:      |________________________________|
   * IST:                                            |----------------|
   * Version:                                                1
   * <p>
   * RESULTAT: |________________________________|    |----------------|     NEUE VERSION EINGEFÜGT
   * Version:                 2                              1
   */
  @Test
  void scenario7d() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = version1Builder().build();
    editedVersion.setDescriptionOutwardLine1("FPFN Description <changed>");
    editedVersion.setValidFrom(LocalDate.of(2019, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2019, 5, 31));
    editedVersion.setVersion(version1.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version1, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.findBySid4ptOrderByValidFrom(version1.getTtfnid());

    //then
    assertThat(result).hasSize(4);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    //first new
    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2019, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2019, 5, 31));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // first version no changes
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // second version no changes
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(thirdTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // third version no changes
    TimetableFieldNumberVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fourthTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(fourthTemporalVersion.getNumber()).isEqualTo("80.099.3");
    assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
  }

  /**
   * ValidFrom is bigger than validTo: expected exception
   */
  @Test
  void scenarioValidFromBiggerThenValidTo() {
    //given
    version1.setValidFrom(LocalDate.of(2020, 12, 12));
    version1.setValidTo(LocalDate.of(2029, 12, 8));
    version1 = versionRepository.save(version1);
    TimetableFieldNumberVersion editedVersion = version1Builder().build();
    editedVersion.setDescriptionOutwardLine1("FPFN Description Frederic");
    editedVersion.setValidFrom(LocalDate.of(2029, 12, 9));
    editedVersion.setValidTo(version1.getValidTo());
    editedVersion.setVersion(version1.getVersion());

    //when
    assertThatThrownBy(() -> {
      timetableFieldNumberService.updateVersion(version1, editedVersion);
      //then
    }).isInstanceOf(DateOrderException.class)
        .hasMessageContaining("Edited ValidFrom is bigger than edited ValidTo")
        .extracting("validFrom", "validTo")
        .containsExactly(LocalDate.of(2029, 12, 9), LocalDate.of(2029, 12, 8));
  }
}