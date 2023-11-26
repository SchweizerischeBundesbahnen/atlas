package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.entity.SublineVersion.SublineVersionBuilder;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
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
 class SublineServiceVersioningTest {

  private static final String DESCRIPTION = SublineTestData.sublineVersion().getDescription();

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  private static final String SLNID = "ch:1:slnid:100000";
  private final SublineVersionRepository sublineVersionRepository;
  private final LineVersionRepository lineVersionRepository;
  private final SublineService sublineService;
  private SublineVersion version1;
  private SublineVersion version2;
  private SublineVersion version3;

  @Autowired
   SublineServiceVersioningTest(
      SublineVersionRepository sublineVersionRepository,
      LineVersionRepository lineVersionRepository,
      SublineService sublineService) {
    this.sublineVersionRepository = sublineVersionRepository;
    this.lineVersionRepository = lineVersionRepository;
    this.sublineService = sublineService;
  }

  @BeforeEach
  void init() {
    LineVersion lineVersion = LineTestData.lineVersionBuilder()
                                    .slnid(SublineTestData.MAINLINE_SLNID)
                                    .validFrom(LocalDate.of(2020, 1, 1))
                                    .validTo(LocalDate.of(2025, 12, 31))
                                    .build();
    lineVersionRepository.save(lineVersion);
    version1 = version1Builder()                              .build();
    version2 = version2Builder()                              .build();
    version3 = version3Builder()                              .build();
  }

  private static SublineVersionBuilder<?, ?> version3Builder() {
    return SublineTestData.sublineVersionBuilder().slnid(SLNID)
        .swissSublineNumber("3")
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31));
  }

  private static SublineVersionBuilder<?, ?> version2Builder() {
    return SublineTestData.sublineVersionBuilder().slnid(SLNID)
        .swissSublineNumber("2")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31));
  }

  private static SublineVersionBuilder<?, ?> version1Builder() {
    return SublineTestData.sublineVersionBuilder().slnid(SLNID)
        .swissSublineNumber("1")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31));
  }

  @AfterEach
  void cleanUp() {
    sublineVersionRepository.deleteAll();
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
    version1 = sublineVersionRepository.save(version1);
    version2 = sublineVersionRepository.save(version2);
    version3 = sublineVersionRepository.save(version3);
    SublineVersion editedVersion = version2Builder().build();
    editedVersion.setDescription("Description <changed>");
    editedVersion.setValidFrom(LocalDate.of(2022, 6, 1));
    editedVersion.setValidTo(LocalDate.of(2023, 6, 1));

    //when
    sublineService.updateVersion(version2, editedVersion);
    List<SublineVersion> result = sublineVersionRepository.findAllBySlnidOrderByValidFrom(
        version1.getSlnid());

    //then

    assertThat(result).isNotNull();
    assertThat(result).hasSize(5);
    result.sort(Comparator.comparing(SublineVersion::getValidFrom));
    assertThat(result.get(0)).isNotNull();

    SublineVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getDescription()).isEqualTo(DESCRIPTION);

    //updated
    SublineVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2022, 5, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo(DESCRIPTION);

    //new
    SublineVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 6, 1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 6, 1));
    assertThat(thirdTemporalVersion.getDescription()).isEqualTo("Description <changed>");

    //new
    SublineVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2023, 6, 2));
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(fourthTemporalVersion.getDescription()).isEqualTo(DESCRIPTION);

    //current
    SublineVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(fifthTemporalVersion.getDescription()).isEqualTo(DESCRIPTION);
  }


  /**
   * Merge zwei versionen
   *
   * NEU:                 |__________|
   *                        number=2
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
    version1.setSwissSublineNumber("1");
    version1 = sublineVersionRepository.save(version1);
    version2.setSwissSublineNumber("3");
    version2 = sublineVersionRepository.save(version2);
    version3.setSwissSublineNumber("2");
    version3 = sublineVersionRepository.save(version3);
    SublineVersion editedVersion = version2Builder().build();
    editedVersion.setSwissSublineNumber("2");

    //when
    sublineService.updateVersion(version2, editedVersion);
    List<SublineVersion> result = sublineVersionRepository.findAllBySlnidOrderByValidFrom(
        version1.getSlnid());

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    result.sort(Comparator.comparing(SublineVersion::getValidFrom));

    // first version no changes
    assertThat(result.get(0)).isNotNull();
    SublineVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(version1.getValidFrom());
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(version1.getValidTo());
    assertThat(firstTemporalVersion.getSwissSublineNumber()).isEqualTo("1");

    // second merged with third
    SublineVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(version2.getValidFrom());
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(version3.getValidTo());
    assertThat(secondTemporalVersion.getSwissSublineNumber()).isEqualTo("2");
  }
}