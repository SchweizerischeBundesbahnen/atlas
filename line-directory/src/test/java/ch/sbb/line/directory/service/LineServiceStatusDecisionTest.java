package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersion.LineVersionBuilder;
import ch.sbb.line.directory.repository.LineVersionRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
 class LineServiceStatusDecisionTest {

  private static final String SLNID = "ch:1:slnid:100000";

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  private final LineVersionRepository lineVersionRepository;
  private final LineService lineService;

  private LineVersion version1;
  private LineVersion version2;
  private LineVersion version3;

  @Autowired
   LineServiceStatusDecisionTest(
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
        .number("3")
        .status(Status.VALIDATED)
        .colorBackRgb(LineTestData.RBG_RED)
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31));
  }

  private static LineVersionBuilder<?, ?> version2Builder() {
    return LineTestData.lineVersionBuilder().slnid(SLNID)
        .swissLineNumber("2")
        .number("2")
        .status(Status.VALIDATED)
        .colorBackRgb(LineTestData.RGB_BLACK)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31));
  }

  private static LineVersionBuilder<?, ?> version1Builder() {
    return LineTestData.lineVersionBuilder().slnid(SLNID)
        .swissLineNumber("1")
        .number("1")
        .status(Status.VALIDATED)
        .colorBackRgb(LineTestData.RBG_YELLOW)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31));
  }

  @AfterEach
  void cleanUp() {
    lineVersionRepository.deleteAll();
  }

  @Test
   void newlyCreatedOrderlyVersionShouldRequireWorkflow() {
    //given
    LineVersion lineVersion = version1;
    lineVersion.setLineType(LineType.ORDERLY);

    //when
    lineService.create(lineVersion);
    List<LineVersion> result = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        lineVersion.getSlnid());

    //then

    assertThat(result).isNotNull().hasSize(1);

    // Version 1
    LineVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.DRAFT);
  }

  @Test
   void newlyCreatedOperationalVersionShouldNotRequireWorkflow() {
    //given
    LineVersion lineVersion = version1;
    lineVersion.setLineType(LineType.OPERATIONAL);

    //when
    lineService.create(lineVersion);
    List<LineVersion> result = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        lineVersion.getSlnid());

    //then

    assertThat(result).isNotNull().hasSize(1);

    // Version 1
    LineVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
  }

  @Test
   void updateCreatingNewFeatureVersionAndReupdateShouldStayAsDraft() {
    //given
    version1 = lineVersionRepository.save(version1);
    LineVersion editedVersion = version1Builder().build();
    editedVersion.setDescription("Description <changed>");
    editedVersion.setValidFrom(LocalDate.of(2022, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2022, 12, 31));

    //when
    lineService.updateVersion(version1, editedVersion);
    List<LineVersion> result = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        version1.getSlnid());

    //then

    assertThat(result).isNotNull().hasSize(2);

    // Version 1
    LineVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);

    // Version 2
    LineVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2022, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("Description <changed>");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.DRAFT);

    editedVersion = version1Builder().build();
    editedVersion.setColorBackRgb(LineTestData.RBG_RED);
    editedVersion.setValidFrom(LocalDate.of(2022, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2022, 12, 31));

    // when DRAFT Version gets updated again
    lineService.updateVersion(version1, editedVersion);
    result = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        version1.getSlnid());

    secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.DRAFT);
  }

  /**
   * Szenario 1: Neue Version mit neuem Namen wird hinzugefügt
   * Vorher:      |-------------|
   * Version:            1
   * <p>
   * Nachher:     |-------------|______|
   * Version:            1         2
   * <p>
   * Resultat:
   *  - Worflow auf 1 nicht nötig, da nicht verändert.
   *  - Worflow auf 2 nötig, da Name neu und noch nie genehmigt.
   */
  @Test
   void updateCreatingNewFeatureVersion() {
    //given
    version1 = lineVersionRepository.save(version1);
    LineVersion editedVersion = version1Builder().build();
    editedVersion.setDescription("Description <changed>");
    editedVersion.setValidFrom(LocalDate.of(2022, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2022, 12, 31));

    //when
    lineService.updateVersion(version1, editedVersion);
    List<LineVersion> result = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        version1.getSlnid());

    //then

    assertThat(result).isNotNull().hasSize(2);

    // Version 1
    LineVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);

    // Version 2
    LineVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2022, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("Description <changed>");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.DRAFT);
  }

  /**
   * Szenario 1: Name wird "hinten" geändert
   * Vorher:      |-------------|
   * Version:            1
   * <p>
   * Nachher:     |------|______|
   * Version:        1       2
   * <p>
   * Resultat:
   *  - Worflow auf 1 nicht nötig, da nur eingekürzt.
   *  - Worflow auf 2 nötig, da Name im Vergleich zu vorher geändert hat.
   */
  @Test
   void updateScenario1() {
    //given
    version1 = lineVersionRepository.save(version1);
    LineVersion editedVersion = version1Builder().build();
    editedVersion.setDescription("Description <changed>");
    editedVersion.setComment("Scenario 1");
    editedVersion.setValidFrom(LocalDate.of(2021, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2021, 12, 31));

    //when
    lineService.updateVersion(version1, editedVersion);
    List<LineVersion> result = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        version1.getSlnid());

    //then

    assertThat(result).isNotNull().hasSize(2);

    // Version 1
    LineVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 12, 31));
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);

    // Version 2
    LineVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2021, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("Description <changed>");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.DRAFT);
  }

  /**
   * Szenario 1: Name wird "vorne" geändert
   * Vorher:      |-------------|
   * Version:            1
   * <p>
   * Nachher:     |______|------|
   * Version:        1       2
   * <p>
   * Resultat:
   *  - Worflow auf 1 nötig, da Name im Vergleich zu vorher geändert hat.
   *  - Worflow auf 2 nicht nötig, da nur eingekürzt.
   */
  @Test
   void updateScenario1a() {
    //given
    version1 = lineVersionRepository.save(version1);
    LineVersion editedVersion = version1Builder().build();
    editedVersion.setDescription("Description <changed>");
    editedVersion.setComment("Scenario 1");
    editedVersion.setValidFrom(LocalDate.of(2020, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2020, 1, 1));

    //when
    lineService.updateVersion(version1, editedVersion);
    List<LineVersion> result = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        version1.getSlnid());

    //then

    assertThat(result).isNotNull().hasSize(2);

    // Version 1
    LineVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.DRAFT);

    // Version 2
    LineVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 2));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(version1.getValidTo());
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
  }

  /**
   * Szenario: Name wird "hinten" geändert und verlängert
   * Vorher:      |-------------|
   * Version:            1
   * <p>
   * Nachher:     |------|___________|
   * Version:        1       2
   * <p>
   * Resultat:
   *  - Worflow auf 1 nicht nötig, da nur eingekürzt.
   *  - Worflow auf 2 nötig, da Name im Vergleich zu vorher geändert hat.
   */
  @Test
   void updateScenario1b() {
    //given
    version1 = lineVersionRepository.save(version1);
    LineVersion editedVersion = version1Builder().build();
    editedVersion.setDescription("Description <changed>");
    editedVersion.setValidFrom(LocalDate.of(2021, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2022, 12, 31));

    //when
    lineService.updateVersion(version1, editedVersion);
    List<LineVersion> result = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        version1.getSlnid());

    //then
    assertThat(result).isNotNull().hasSize(2);

    // Version 1
    LineVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 12, 31));
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);

    // Version 2
    LineVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2021, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2022, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("Description <changed>");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.DRAFT);
  }

  /**
   * Szenario: Lücke wird aus- und überfüllt
   * Vorher:      |-------------|      |-------------|
   * Version:            1                    2
   * <p>
   * Nachher:     |------------|_______|-------------|
   * Version:           1          2          3
   * <p>
   * Resultat:
   *  - Worflow auf 1 & 3 nicht nötig, da nur eingekürzt.
   *  - Worflow auf 2 nötig, da Name im Vergleich zu vorher geändert hat.
   */
  @Test
   void updateScenarioVersioning10c() {
    //given
    version1 = lineVersionRepository.save(version1);
    version3 = lineVersionRepository.save(version3);

    LineVersion editedVersion = version3Builder().build();
    editedVersion.setSwissLineNumber("2");
    editedVersion.setNumber("2");
    editedVersion.setColorBackRgb(LineTestData.RGB_BLACK);

    editedVersion.setValidFrom(LocalDate.of(2021, 12, 31));
    editedVersion.setValidTo(LocalDate.of(2024, 1, 1));

    //when
    lineService.updateVersion(version3, editedVersion);
    List<LineVersion> result = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        version1.getSlnid());

    //then
    assertThat(result).isNotNull().hasSize(3);

    // Version 1
    LineVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 30));
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);

    // Version 2
    LineVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(editedVersion.getValidFrom());
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(editedVersion.getValidTo());
    assertThat(secondTemporalVersion.getSwissLineNumber()).isEqualTo("2");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.DRAFT);

    // Version 3
    LineVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(editedVersion.getValidTo().plusDays(1));
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(version3.getValidTo());
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
  }

  /**
   * Szenario: Gültigkeit wird eingekürzt
   * Vorher:      |-------------|
   * Version:            1
   * <p>
   * Nachher:         |_____|
   * Version:            1
   * <p>
   * Resultat:
   *  - Worflow auf 1 nicht nötig da nur eingekürzt
   */
  @Test
   void updateScenarioVersioning14b() {
    //given
    version1 = lineVersionRepository.saveAndFlush(version1);
    LineVersion editedVersion = version1Builder().build();
    editedVersion.setCreationDate(version1.getCreationDate());
    editedVersion.setCreator(version1.getCreator());
    editedVersion.setEditionDate(version1.getEditionDate());
    editedVersion.setEditor(version1.getEditor());
    editedVersion.setVersion(version1.getVersion());

    editedVersion.setValidFrom(LocalDate.of(2020, 7, 1));
    editedVersion.setValidTo(LocalDate.of(2021, 7, 1));

    //when
    lineService.updateVersion(version1, editedVersion);
    List<LineVersion> result = lineVersionRepository.findAllBySlnidOrderByValidFrom(version1.getSlnid());

    //then
    assertThat(result).isNotNull().hasSize(1);

    // Version 1
    LineVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(editedVersion.getValidFrom());
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(editedVersion.getValidTo());
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
  }

  /**
   * Szenario 1: Name wird in der Mitte verändert
   * Vorher:      |-------------|-------------|-------------|
   * Version:            1             2            3
   * <p>
   * Nachher:     |-------------|_____________|-------------|
   * Version:            1             2             3
   * <p>
   * Resultat:
   *  - Worflow auf 1 & 3 nicht nötig, da nicht berührt
   *  - Worflow auf 2 nötig, da Name im Vergleich zu vorher geändert.
   */
  @Test
   void updateScenario2() {
    //given
    version1 = lineVersionRepository.save(version1);
    version2 = lineVersionRepository.save(version2);
    version3 = lineVersionRepository.save(version3);

    LineVersion editedVersion = version2Builder().build();
    editedVersion.setNumber("4");
    editedVersion.setValidFrom(version2.getValidFrom());
    editedVersion.setValidTo(version2.getValidTo());

    //when
    lineService.updateVersion(version2, editedVersion);
    List<LineVersion> result = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        version1.getSlnid());

    //then

    assertThat(result).isNotNull().hasSize(3);

    // Version 1
    LineVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(version1.getValidFrom());
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(version1.getValidTo());
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);

    // Version 2
    LineVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(version2.getValidFrom());
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(version2.getValidTo());
    assertThat(secondTemporalVersion.getNumber()).isEqualTo("4");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.DRAFT);

    // Version 3
    LineVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(version3.getValidFrom());
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(version3.getValidTo());
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
  }

  /**
   * Szenario 1: Name wird in der Mitte verlängert
   * Vorher:      |-------------|-------------|-------------|
   * Version:            1             2            3
   * <p>
   * Nachher:     |------|______|_____________|______|------|
   * Version:        1      2          3          4      5
   * <p>
   * Resultat:
   *  - Worflow auf 1 & 5 nicht nötig, da nur eingekürzt.
   *  - Worflow auf 2 & 4 nötig, da Name im Vergleich zur Version vorher im Zeitraum eine Veränderung im Namen existiert
   *  - Worflow auf 3 nicht nötig, da Name im Vergleich zur Version vorher im Zeitraum nicht geändert
   */
  @Test
   void updateScenario3() {
    //given
    version1 = lineVersionRepository.save(version1);
    version2 = lineVersionRepository.save(version2);
    version3 = lineVersionRepository.save(version3);

    LineVersion editedVersion = version3Builder().build();
    editedVersion.setValidFrom(LocalDate.of(2020, 7, 1));
    editedVersion.setValidTo(LocalDate.of(2024, 7, 31));
    editedVersion.setNumber("2");

    //when
    lineService.updateVersion(version3, editedVersion);
    List<LineVersion> result = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        version1.getSlnid());

    //then

    assertThat(result).isNotNull().hasSize(5);

    // Version 1
    LineVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(version1.getValidFrom());
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(editedVersion.getValidFrom().minusDays(1));
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);

    // Version 2
    LineVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(editedVersion.getValidFrom());
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(version2.getValidFrom().minusDays(1));
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.DRAFT);

    // Version 3
    LineVersion thirdTemporalVersion = result.get(2);
    assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(version2.getValidFrom());
    assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(version2.getValidTo());
    assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);

    // Version 4
    LineVersion fourthTemporalVersion = result.get(3);
    assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(version3.getValidFrom());
    assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(editedVersion.getValidTo());
    assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.DRAFT);

    // Version 5
    LineVersion fifthTemporalVersion = result.get(4);
    assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(editedVersion.getValidTo().plusDays(1));
    assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(version3.getValidTo());
    assertThat(fifthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
  }
}