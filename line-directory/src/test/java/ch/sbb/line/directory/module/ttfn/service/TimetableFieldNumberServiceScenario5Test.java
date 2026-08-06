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

class TimetableFieldNumberServiceScenario5Test extends BaseTimetableFieldNumberServiceTest {

  @Autowired
  TimetableFieldNumberServiceScenario5Test(
      TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberService timetableFieldNumberService) {
    super(versionRepository, timetableFieldNumberService);
  }

  /**
   * Szenario 5: Update, das über mehrere Versionen hinausragt
   * <p>
   * NEU:             |___________________________________|
   * IST:      |-----------|-----------|-----------|-------------------
   * Version:        1           2          3               4
   * <p>
   * RESULTAT: |------|_____|__________|____________|_____|------------     NEUE VERSION EINGEFÜGT
   * Version:      1     5       2           3         6      4
   */
  @Test
  void scenario5() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    version4 = versionRepository.save(version4);
    TimetableFieldNumberVersion editedVersion = version3Builder().build();
    editedVersion.setDescriptionOutwardLine1("FPFN Description <changed>");
    editedVersion.setValidFrom(LocalDate.of(2020, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2025, 6, 1));
    editedVersion.setVersion(version3.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version3, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.findBySid4ptOrderByValidFrom(version1.getTtfnid());

    //then
    assertThat(result).hasSize(6);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    // first current index updated
    assertThat(result.getFirst()).isNotNull();
    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 5, 31));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    // new
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 6, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    //update
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(thirdTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    //new
    TimetableFieldNumberVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fourthTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(fourthTemporalVersion.getNumber()).isEqualTo("80.099.3");
    assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    //new
    TimetableFieldNumberVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 6, 1));
    assertThat(fifthTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    assertThat(fifthTemporalVersion.getNumber()).isEqualTo("80.099.4");
    assertThat(fifthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fifthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");

    //last current index updated
    TimetableFieldNumberVersion sixthTemporalVersion = result.get(5);
    assertThat(sixthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 6, 2));
    assertThat(sixthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(sixthTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(sixthTemporalVersion.getNumber()).isEqualTo("80.099.4");
    assertThat(sixthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(sixthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
  }
}