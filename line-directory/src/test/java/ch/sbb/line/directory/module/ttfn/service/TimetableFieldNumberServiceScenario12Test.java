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

class TimetableFieldNumberServiceScenario12Test extends BaseTimetableFieldNumberServiceTest {

  @Autowired
  TimetableFieldNumberServiceScenario12Test(
      TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberService timetableFieldNumberService) {
    super(versionRepository, timetableFieldNumberService);
  }

  /**
   * Scenario 12 (Spezialfall 2): Update während und nach letzter Version
   * <p>
   * Änderung                          |_____________________|
   * |-----------------|-------------|
   * Version 1       Version 2
   * <p>
   * Ergebnis: Version 2 wird verlängert
   */
  @Test
  void scenario12() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    TimetableFieldNumberVersion editedVersion = version1Builder().build();
    editedVersion.setValidFrom(version2.getValidFrom());
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
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
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

}
