package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.repository.LineVersionRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class LineServiceStatusDecisionTest {

  private static final String SLNID = "ch:1:slnid:100000";

  private final LineVersionRepository lineVersionRepository;
  private final LineService lineService;

  private LineVersion version1;
  private LineVersion version2;
  private LineVersion version3;

  @Autowired
  public LineServiceStatusDecisionTest(
      LineVersionRepository lineVersionRepository,
      LineService lineService) {
    this.lineVersionRepository = lineVersionRepository;
    this.lineService = lineService;
  }

  @BeforeEach
  void init() {
    version1 = LineTestData.lineVersionBuilder().slnid(SLNID)
        .swissLineNumber("1")
        .number("1")
        .colorBackRgb(LineTestData.RBG_YELLOW)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    version2 = LineTestData.lineVersionBuilder().slnid(SLNID)
        .swissLineNumber("2")
        .number("2")
        .colorBackRgb(LineTestData.RGB_BLACK)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31))
        .build();
    version3 = LineTestData.lineVersionBuilder().slnid(SLNID)
        .swissLineNumber("3")
        .number("3")
        .colorBackRgb(LineTestData.RBG_RED)
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31))
        .build();
  }

  @AfterEach
  void cleanUp() {
    lineVersionRepository.deleteAll();
  }

  @Test
  public void newlyCreatedOrderlyVersionShouldRequireWorkflow() {
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
  public void newlyCreatedOperationalVersionShouldNotRequireWorkflow() {
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
  public void updateCreatingNewFeatureVersionAndReupdateShouldStayAsDraft() {
    //given
    version1 = lineVersionRepository.save(version1);
    LineVersion editedVersion = new LineVersion();
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

    editedVersion = new LineVersion();
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
  public void updateCreatingNewFeatureVersion() {
    //given
    version1 = lineVersionRepository.save(version1);
    LineVersion editedVersion = new LineVersion();
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
  public void updateScenario1() {
    //given
    version1 = lineVersionRepository.save(version1);
    LineVersion editedVersion = new LineVersion();
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
  public void updateScenario2() {
    //given
    version1 = lineVersionRepository.save(version1);
    version2 = lineVersionRepository.save(version2);
    version3 = lineVersionRepository.save(version3);

    LineVersion editedVersion = new LineVersion();
    editedVersion.setNumber("4");

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
  public void updateScenario3() {
    //given
    version1 = lineVersionRepository.save(version1);
    version2 = lineVersionRepository.save(version2);
    version3 = lineVersionRepository.save(version3);

    LineVersion editedVersion = new LineVersion();
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