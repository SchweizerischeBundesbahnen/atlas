package ch.sbb.line.directory.module.ttfn.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldLineRelation;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.module.ttfn.repository.TimetableFieldNumberVersionRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TimetableFieldNumberServiceScenario2Test extends BaseTimetableFieldNumberServiceTest {

  @Autowired
  TimetableFieldNumberServiceScenario2Test(
      TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberService timetableFieldNumberService) {
    super(versionRepository, timetableFieldNumberService);
  }

  /**
   * Szenario 2: Update innerhalb existierender Version
   * NEU:                       |___________|
   * IST:      |-----------|----------------------|--------------------
   * Version:        1                 2                  3
   * <p>
   * RESULTAT: |-----------|----|___________|-----|--------------------     NEUE VERSION EINGEFÜGT
   * Version:        1       2         4       5          3
   */
  @Test
  void scenario2() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);
    TimetableFieldNumberVersion editedVersion = version2Builder().build();
    editedVersion.setDescriptionOutwardLine1("FPFN Description <changed>");
    editedVersion.setStatus(Status.VALIDATED);
    editedVersion.setValidFrom(LocalDate.of(2022, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2023, 6, 1));
    editedVersion.getLineRelations()
        .add(TimetableFieldLineRelation.builder().slnid("ch:1:ttfnid:111111").timetableFieldNumberVersion(version2).build());
    editedVersion.setVersion(version2.getVersion());

    //when
    timetableFieldNumberService.updateVersion(version2, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).hasSize(5);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));
    assertThat(result.getFirst()).isNotNull();

    TimetableFieldNumberVersion firstTemporalVersion = result.getFirst();
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("80.099.1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);

    //updated
    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2022, 5, 31));
    assertThat(secondTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);

    //new
    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 6, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 6, 1));
    assertThat(thirdTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description <changed>");
    Set<TimetableFieldLineRelation> lineRelations = thirdTemporalVersion.getLineRelations();
    assertThat(lineRelations).hasSize(1);
    TimetableFieldLineRelation lineRelation = lineRelations.stream().iterator().next();
    assertThat(lineRelation).isNotNull();
    assertThat(lineRelation.getSlnid()).isEqualTo("ch:1:ttfnid:111111");
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);

    //new
    TimetableFieldNumberVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2023, 6, 2));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(fourthTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(fourthTemporalVersion.getLineRelations()).isEmpty();
    assertThat(fourthTemporalVersion.getNumber()).isEqualTo("80.099.2");
    assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(fourthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);

    //current
    TimetableFieldNumberVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fifthTemporalVersion.getDescriptionOutwardLine1()).isEqualTo("FPFN Description");
    assertThat(fifthTemporalVersion.getLineRelations()).isEmpty();
    assertThat(fifthTemporalVersion.getNumber()).isEqualTo("80.099.3");
    assertThat(fifthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fifthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(fifthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo(SWISS_TIMETABLE_FIELD_NUMBER);
  }

}
