package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.entity.TimetableFieldLineRelation;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

 class TimetableFieldNumberServiceScenario1Test extends BaseTimetableFieldNumberServiceTest {

  @Autowired
   TimetableFieldNumberServiceScenario1Test(
      TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberService timetableFieldNumberService) {
    super(versionRepository, timetableFieldNumberService);
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
   void scenario1a() {
    //given
    version2.setLineRelations(new HashSet<>(
        Set.of(TimetableFieldLineRelation.builder().slnid(TTFNID).timetableFieldNumberVersion(version2).build(),
            TimetableFieldLineRelation.builder().slnid(TTFNID).timetableFieldNumberVersion(version2).build())));
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);

    TimetableFieldNumberVersion editedVersion = version2Builder().build();
    editedVersion.setDescription("FPFN Description <CHANGED>");
    editedVersion.getLineRelations()
                 .add(TimetableFieldLineRelation.builder().slnid("ch:1:slnid:111111").timetableFieldNumberVersion(version2).build());
    //when
    timetableFieldNumberService.updateVersion(version2, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version2.getTtfnid());

    //then
    assertThat(result).isNotNull().hasSize(2);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    TimetableFieldNumberVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("BEX1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(firstTemporalVersion.getComment()).isNull();

    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("FPFN Description <CHANGED>");
    assertThat(secondTemporalVersion.getLineRelations()).isNotEmpty().hasSize(1);
    Set<TimetableFieldLineRelation> lineRelations = secondTemporalVersion.getLineRelations();
    TimetableFieldLineRelation lineRelation = lineRelations.stream().iterator().next();
    assertThat(lineRelation).isNotNull();
    assertThat(lineRelation.getSlnid()).isEqualTo("ch:1:slnid:111111");
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("BEX2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(secondTemporalVersion.getComment()).isNull();
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
   void scenario1aEditedValidFromAndEditedValidToAreEqualsToCurrentValidFromAndCurrentValidTo() {
    //given
    version2.setLineRelations(new HashSet<>(
        Set.of(TimetableFieldLineRelation.builder().slnid(TTFNID).timetableFieldNumberVersion(version2).build(),
            TimetableFieldLineRelation.builder().slnid(TTFNID).timetableFieldNumberVersion(version2).build())));
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);

    TimetableFieldNumberVersion editedVersion = version2Builder().build();
    editedVersion.setDescription("FPFN Description <CHANGED>");
    editedVersion.setValidFrom(version2.getValidFrom());
    editedVersion.setValidTo(version2.getValidTo());
    editedVersion.getLineRelations()
                 .add(TimetableFieldLineRelation.builder().slnid("ch:1:slnid:111111").timetableFieldNumberVersion(version2).build());
    //when
    timetableFieldNumberService.updateVersion(version2, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version2.getTtfnid());

    //then
    assertThat(result).isNotNull().hasSize(2);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    TimetableFieldNumberVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("BEX1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(firstTemporalVersion.getComment()).isNull();

    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("FPFN Description <CHANGED>");
    assertThat(secondTemporalVersion.getLineRelations()).isNotEmpty().hasSize(1);
    Set<TimetableFieldLineRelation> lineRelations = secondTemporalVersion.getLineRelations();
    TimetableFieldLineRelation lineRelation = lineRelations.stream().iterator().next();
    assertThat(lineRelation).isNotNull();
    assertThat(lineRelation.getSlnid()).isEqualTo("ch:1:slnid:111111");
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("BEX2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(secondTemporalVersion.getComment()).isNull();
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
   void scenario1b() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);

    TimetableFieldNumberVersion editedVersion = version2Builder().build();
    editedVersion.setDescription("FPFN Description <changed>");

    //when
    timetableFieldNumberService.updateVersion(version2, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version2.getTtfnid());

    //then
    assertThat(result).isNotNull().hasSize(3);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    TimetableFieldNumberVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("BEX1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(firstTemporalVersion.getComment()).isNull();

    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("BEX2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(secondTemporalVersion.getComment()).isNull();

    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(thirdTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("BEX3");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
    assertThat(thirdTemporalVersion.getComment()).isNull();
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
   void scenario1c() {
    //given
    version1 = versionRepository.save(version1);
    version2 = versionRepository.save(version2);
    version3 = versionRepository.save(version3);

    TimetableFieldNumberVersion editedVersion = version1Builder().build();
    editedVersion.setDescription("FPFN Description <changed>");
    editedVersion.setComment("A comment");

    //when
    timetableFieldNumberService.updateVersion(version1, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull().hasSize(3);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    TimetableFieldNumberVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescription()).isEqualTo("FPFN Description <changed>");
    assertThat(firstTemporalVersion.getComment()).isEqualTo("A comment");
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("BEX1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("BEX2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(thirdTemporalVersion.getDescription()).isEqualTo("FPFN Description");
    assertThat(thirdTemporalVersion.getComment()).isNull();
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("BEX3");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");
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
   void scenario1d() {
    //given
    version1.setDescription("SBB1");
    version1 = versionRepository.save(version1);
    version2.setDescription("SBB2");
    version2 = versionRepository.save(version2);
    version3.setDescription("SBB3");
    version3 = versionRepository.save(version3);
    version4.setDescription("SBB1");
    version4 = versionRepository.save(version4);
    version5.setDescription("SBB4");
    version5 = versionRepository.save(version5);
    TimetableFieldNumberVersion editedVersion = version2Builder().build();
    editedVersion.setDescription("SBB1");
    editedVersion.setValidFrom(version2.getValidFrom());
    editedVersion.setValidTo(version3.getValidTo());

    //when
    timetableFieldNumberService.updateVersion(version2, editedVersion);
    List<TimetableFieldNumberVersion> result = versionRepository.getAllVersionsVersioned(version1.getTtfnid());

    //then
    assertThat(result).isNotNull().hasSize(5);
    result.sort(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom));

    TimetableFieldNumberVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescription()).isEqualTo("SBB1");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getLineRelations()).isEmpty();
    assertThat(firstTemporalVersion.getNumber()).isEqualTo("BEX1");
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(firstTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    TimetableFieldNumberVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("SBB1");
    assertThat(secondTemporalVersion.getComment()).isNull();
    assertThat(secondTemporalVersion.getLineRelations()).isEmpty();
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("BEX2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(secondTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    TimetableFieldNumberVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(thirdTemporalVersion.getDescription()).isEqualTo("SBB1");
    assertThat(thirdTemporalVersion.getComment()).isNull();
    assertThat(thirdTemporalVersion.getLineRelations()).isEmpty();
    assertThat(thirdTemporalVersion.getNumber()).isEqualTo("BEX3");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(thirdTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    TimetableFieldNumberVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(fourthTemporalVersion.getDescription()).isEqualTo("SBB1");
    assertThat(fourthTemporalVersion.getComment()).isNull();
    assertThat(fourthTemporalVersion.getLineRelations()).isEmpty();
    assertThat(fourthTemporalVersion.getNumber()).isEqualTo("BEX4");
    assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(fourthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

    TimetableFieldNumberVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2026, 1, 1));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2026, 12, 31));
    assertThat(fifthTemporalVersion.getDescription()).isEqualTo("SBB4");
    assertThat(fifthTemporalVersion.getComment()).isNull();
    assertThat(fifthTemporalVersion.getLineRelations()).isEmpty();
    assertThat(fifthTemporalVersion.getNumber()).isEqualTo("BEX5");
    assertThat(fifthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fifthTemporalVersion.getBusinessOrganisation()).isEqualTo("sbb");
    assertThat(fifthTemporalVersion.getSwissTimetableFieldNumber()).isEqualTo("b0.BEX");

  }

}
