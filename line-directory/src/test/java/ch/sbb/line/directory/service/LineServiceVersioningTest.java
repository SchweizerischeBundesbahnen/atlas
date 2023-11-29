package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersion.LineVersionBuilder;
import ch.sbb.line.directory.repository.LineVersionRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
 class LineServiceVersioningTest {

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  private static final String DESCRIPTION = LineTestData.lineVersion().getDescription();

  private static final String SLNID = "ch:1:slnid:100000";
  private final LineVersionRepository lineVersionRepository;
  private final LineService lineService;
  private LineVersion version1;
  private LineVersion version2;
  private LineVersion version3;

  @Autowired
   LineServiceVersioningTest(
      LineVersionRepository lineVersionRepository,
      LineService lineService) {
    this.lineVersionRepository = lineVersionRepository;
    this.lineService = lineService;
  }

  @BeforeEach
  void init() {
    version1 = version1Builder().build();
    version2 = version2Builder().build();
    version3 = version3Builder().build();
  }

  private static LineVersionBuilder<?, ?> version3Builder() {
    return LineTestData.lineVersionBuilder().slnid(SLNID)
        .swissLineNumber("3")
        .comment(null)
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31));
  }

  private static LineVersionBuilder<?, ?> version2Builder() {
    return LineTestData.lineVersionBuilder().slnid(SLNID)
        .swissLineNumber("2")
        .comment(null)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31));
  }

  private static LineVersionBuilder<?, ?> version1Builder() {
    return LineTestData.lineVersionBuilder().slnid(SLNID)
        .swissLineNumber("1")
        .comment(null)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31));
  }

  @AfterEach
  void cleanUp() {
    lineVersionRepository.deleteAll();
  }

  /**
   * Szenario 2: Update innerhalb existierender Version
   * NEU:                       |___________|
   * IST:      |-----------|----------------------|--------------------
   * Version:        1                 2                  3
   *
   * RESULTAT: |-----------|----|___________|-----|--------------------     NEUE VERSION EINGEFÜGT
   * Version:        1       2         4       5          3
   */
  @Test
   void scenario2() {
    //given
    version1 = lineVersionRepository.save(version1);
    version2 = lineVersionRepository.save(version2);
    version3 = lineVersionRepository.save(version3);
    LineVersion editedVersion = version2Builder().build();
    editedVersion.setDescription("Description <changed>");
    editedVersion.setComment("Scenario 2");
    editedVersion.setValidFrom(LocalDate.of(2022, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2023, 6, 1));

    //when
    lineService.updateVersion(version2, editedVersion);
    List<LineVersion> result = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        version1.getSlnid());

    //then

    assertThat(result).hasSize(5);
    result.sort(Comparator.comparing(LineVersion::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    LineVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(firstTemporalVersion.getComment()).isNull();

    //updated
    LineVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2022, 5, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(secondTemporalVersion.getComment()).isNull();

    //new
    LineVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 6, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 6, 1));
    assertThat(thirdTemporalVersion.getDescription()).isEqualTo("Description <changed>");
    assertThat(thirdTemporalVersion.getComment()).isEqualTo("Scenario 2");

    //new
    LineVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2023, 6, 2));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(fourthTemporalVersion.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(fourthTemporalVersion.getComment()).isNull();

    //current
    LineVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fifthTemporalVersion.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(fifthTemporalVersion.getComment()).isNull();
  }


  /**
   * Merge zwei versionen
   *
   * NEU:                 |__________|
   * number=2
   * IST:      |----------|----------|----------|
   * Version:        1          2          3
   * Änderung:  number=1   number=3  number=2
   *
   * RESULTAT: |----------|--------------------|
   * Version:        1               2
   * Änderung:  name=SBB1       number=2
   */
  @Test
   void scenarioMergeTwoVersions() {
    //given
    version1.setSwissLineNumber("1");
    version1 = lineVersionRepository.save(version1);
    version2.setSwissLineNumber("3");
    version2 = lineVersionRepository.save(version2);
    version3.setSwissLineNumber("2");
    version3 = lineVersionRepository.save(version3);
    LineVersion editedVersion = version2Builder().build();
    editedVersion.setSwissLineNumber("2");
    editedVersion.setValidFrom(version2.getValidFrom());
    editedVersion.setValidTo(version2.getValidTo());

    //when
    lineService.updateVersion(version2, editedVersion);
    List<LineVersion> result = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        version1.getSlnid());

    //then
    assertThat(result).hasSize(2);
    result.sort(Comparator.comparing(LineVersion::getValidFrom));

    // first version no changes
    assertThat(result.get(0)).isNotNull();
    LineVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(version1.getValidFrom());
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(version1.getValidTo());
    assertThat(firstTemporalVersion.getSwissLineNumber()).isEqualTo("1");
    assertThat(firstTemporalVersion.getComment()).isNull();

    // second merged with third
    LineVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(version2.getValidFrom());
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(version3.getValidTo());
    assertThat(secondTemporalVersion.getSwissLineNumber()).isEqualTo("2");
    assertThat(firstTemporalVersion.getComment()).isNull();
  }

  /**
   * Szenario 4: Update, das über eine ganze Version hinausragt
   * NEU:             |___________________________________|
   * IST:      |-----------|----------------------|--------------------
   * Version:        1                 2                  3
   *
   *
   * RESULTAT: |------|_____|______________________|______|------------     NEUE VERSION EINGEFÜGT
   * Version:      1     4              2              5        3
   */
  @Test
   void scenario4() {
    //given
    version1 = lineVersionRepository.save(version1);
    version2 = lineVersionRepository.save(version2);
    version3 = lineVersionRepository.save(version3);
    LineVersion editedVersion = version3Builder().build();
    editedVersion.setDescription("Name <changed>");
    editedVersion.setComment("Scenario 4");
    editedVersion.setValidFrom(LocalDate.of(2020, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2024, 6, 1));

    //when
    lineService.updateVersion(version3, editedVersion);
    List<LineVersion> result = lineService.findLineVersions(version1.getSlnid());

    //then
    assertThat(result).hasSize(5);
    result.sort(Comparator.comparing(LineVersion::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    // first current index updated
    LineVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 5, 31));
    assertThat(firstTemporalVersion.getDescription()).isEqualTo("description");
    assertThat(firstTemporalVersion.getComment()).isNull();
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(firstTemporalVersion.getSwissLineNumber()).isEqualTo("1");

    // new
    LineVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 6, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("Name <changed>");
    assertThat(secondTemporalVersion.getComment()).isEqualTo("Scenario 4");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.DRAFT);
    assertThat(secondTemporalVersion.getSwissLineNumber()).isEqualTo("1");

    //update
    LineVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(thirdTemporalVersion.getDescription()).isEqualTo("Name <changed>");
    assertThat(thirdTemporalVersion.getComment()).isEqualTo("Scenario 4");
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.DRAFT);
    assertThat(thirdTemporalVersion.getSwissLineNumber()).isEqualTo("2");

    //new
    LineVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
    assertThat(fourthTemporalVersion.getDescription()).isEqualTo("Name <changed>");
    assertThat(fourthTemporalVersion.getComment()).isEqualTo("Scenario 4");
    assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.DRAFT);
    assertThat(fourthTemporalVersion.getSwissLineNumber()).isEqualTo("3");

    //last current index updated
    LineVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 6, 2));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fifthTemporalVersion.getDescription()).isEqualTo("description");
    assertThat(fifthTemporalVersion.getComment()).isNull();
    assertThat(fifthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(fifthTemporalVersion.getSwissLineNumber()).isEqualTo("3");

  }
}