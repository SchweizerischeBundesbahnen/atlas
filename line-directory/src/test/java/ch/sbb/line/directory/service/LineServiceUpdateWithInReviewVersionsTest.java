package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersion.LineVersionBuilder;
import ch.sbb.line.directory.exception.LineInReviewValidationException;
import ch.sbb.line.directory.exception.MergeOrSplitInReviewVersionException;
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
public class LineServiceUpdateWithInReviewVersionsTest {

  private static final String SLNID = "ch:1:slnid:100000";

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  private final LineVersionRepository lineVersionRepository;
  private final LineService lineService;

  private LineVersion version1;
  private LineVersion version2;
  private LineVersion version3;

  @Autowired
  public LineServiceUpdateWithInReviewVersionsTest(
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


  /**
   * Szenario 1: Neue Version mit neuem Namen wird hinzugefügt - 1 IN_REVIEW
   * Vorher:      |-------------|
   * Version:            1
   * <p>
   * Nachher:     |-------------|______|
   * Version:            1         2
   * <p>
   * Resultat:
   *  - Möglich, da Version 1 nicht berührt wird
   */
  @Test
  public void updateScenario1() {
    //given
    version1.setStatus(Status.IN_REVIEW);
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
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.IN_REVIEW);

    // Version 2
    LineVersion secondTemporalVersion = result.get(1);
    assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2022, 12, 31));
    assertThat(secondTemporalVersion.getDescription()).isEqualTo("Description <changed>");
    assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.DRAFT);
  }

  /**
   * Szenario 2: Name wird "hinten" geändert - 1 IN_REVIEW
   * Vorher:      |-------------|
   * Version:            1
   * <p>
   * Nachher:     |------|______|
   * Version:        1       2
   * <p>
   * Resultat:
   *  - Nicht möglich, da IN_REVIEW Version validTo verändert wird
   */
  @Test
  public void updateScenario2() {
    //given
    version1.setStatus(Status.IN_REVIEW);
    version1 = lineVersionRepository.save(version1);

    LineVersion editedVersion = version1Builder().build();
    editedVersion.setDescription("Description <changed>");
    editedVersion.setComment("Scenario 1");
    editedVersion.setValidFrom(LocalDate.of(2021, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2021, 12, 31));

    //when
    assertThrows(LineInReviewValidationException.class, () -> lineService.updateVersion(version1, editedVersion));
  }


  /**
   * Szenario 3: Lücke wird aus- und überfüllt - 1 IN_REVIEW
   * Vorher:      |-------------|      |-------------|
   * Version:            1                    2
   * <p>
   * Nachher:     |------------|_______|-------------|
   * Version:           1          2          3
   * <p>
   * Resultat:
   *  - Nicht möglich da auf 1 validTo verändert wird
   */
  @Test
  public void updateScenario3() {
    //given
    version1.setStatus(Status.IN_REVIEW);
    version1 = lineVersionRepository.save(version1);
    version3 = lineVersionRepository.save(version3);

    LineVersion editedVersion = version3Builder().build();
    editedVersion.setSwissLineNumber("2");
    editedVersion.setNumber("2");
    editedVersion.setColorBackRgb(LineTestData.RGB_BLACK);

    editedVersion.setValidFrom(LocalDate.of(2021, 12, 31));
    editedVersion.setValidTo(LocalDate.of(2024, 1, 1));

    //when
    assertThrows(MergeOrSplitInReviewVersionException.class, () -> lineService.updateVersion(version3, editedVersion));
  }

  /**
   * Szenario 4: Gültigkeit wird eingekürzt - 1 IN_REVIEW
   * Vorher:      |-------------|
   * Version:            1
   * <p>
   * Nachher:         |_____|
   * Version:            1
   * <p>
   * Resultat:
   *  - ValidFrom, ValidTo und LineType dürfen nicht verändert werden
   */
  @Test
  public void updateScenario4() {
    //given
    version1.setStatus(Status.IN_REVIEW);
    version1 = lineVersionRepository.save(version1);

    LineVersion editedVersion = version1Builder().build();
    editedVersion.setValidFrom(LocalDate.of(2020, 7, 1));
    editedVersion.setValidTo(LocalDate.of(2021, 7, 1));

    //when
    assertThrows(LineInReviewValidationException.class, () -> lineService.updateVersion(version1, editedVersion));
  }

  /**
   * Szenario 5: Name wird vorne überschneidend geändert - 1 IN_REVIEW
   * Vorher:               |-------------|
   * Version:                     1
   * <p>
   * Nachher:     |__________|-----------|
   * Version:          2           1
   * <p>
   * Resultat:
   *  - Nicht möglich, da IN_REVIEW Version validFrom verändert wird
   */
  @Test
  public void updateScenario5() {
    //given
    version1.setStatus(Status.IN_REVIEW);
    version1 = lineVersionRepository.save(version1);

    LineVersion editedVersion = version1Builder().build();
    editedVersion.setDescription("Description <changed>");
    editedVersion.setComment("Scenario 1");
    editedVersion.setValidFrom(LocalDate.of(2021, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2021, 12, 31));

    //when
    assertThrows(LineInReviewValidationException.class, () -> lineService.updateVersion(version1, editedVersion));
  }

  /**
   * Szenario 6: Gültigkeit wird verlängert und ein Attribut verändert - 1 IN_REVIEW
   * Vorher:          |------|
   * Version:            1
   * <p>
   * Nachher:      |___________|
   * Version:            1
   * <p>
   * Resultat:
   *  - ValidFrom, ValidTo und LineType dürfen nicht verändert werden
   */
  @Test
  public void updateScenario6() {
    //given
    version1.setStatus(Status.IN_REVIEW);
    version1 = lineVersionRepository.save(version1);

    LineVersion editedVersion = version1Builder().build();
    editedVersion.setValidFrom(version1.getValidFrom().minusMonths(1));
    editedVersion.setValidTo(version1.getValidTo().plusMonths(1));

    //when
    assertThrows(LineInReviewValidationException.class, () -> lineService.updateVersion(version1, editedVersion));
  }

  /**
   * Szenario 7: Name wird in der Mitte geändert - 2 IN_REVIEW
   * Vorher:      |-------------|-------------|-------------|
   * Version:            1             2            3
   * <p>
   * Nachher:     |-------------|___|_____|___|-------------|
   * Version:           1         2    3    4       5
   * <p>
   * Resultat:
   *  - Nicht möglich, da Anhörung tangiert
   */
  @Test
  public void updateScenario7() {
    //given
    version1 = lineVersionRepository.save(version1);
    version2.setStatus(Status.IN_REVIEW);
    version2 = lineVersionRepository.save(version2);
    version3 = lineVersionRepository.save(version3);

    LineVersion editedVersion = version3Builder().build();
    editedVersion.setValidFrom(version2.getValidFrom().plusMonths(1));
    editedVersion.setValidTo(version2.getValidTo().minusMonths(1));
    editedVersion.setNumber("7");

    //when
    assertThrows(MergeOrSplitInReviewVersionException.class, () -> lineService.updateVersion(version3, editedVersion));
  }

  /**
   * Szenario 8: Name wird in der Mitte geändert - 1 IN_REVIEW
   * Vorher:      |-------------|-------------|-------------|
   * Version:            1             2            3
   * <p>
   * Nachher:     |-------------|___|_____|___|-------------|
   * Version:           1         2    3    4       5
   * <p>
   * Resultat:
   *  - Möglich, da Anhörung nicht beeinflusst
   */
  @Test
  public void updateScenario8() {
    //given
    version1.setStatus(Status.IN_REVIEW);
    version1 = lineVersionRepository.save(version1);
    version2 = lineVersionRepository.save(version2);
    version3 = lineVersionRepository.save(version3);

    LineVersion editedVersion = version3Builder().build();
    editedVersion.setValidFrom(version2.getValidFrom().plusMonths(1));
    editedVersion.setValidTo(version2.getValidTo().minusMonths(1));
    editedVersion.setNumber("7");

    //when
    assertDoesNotThrow(() -> lineService.updateVersion(version3, editedVersion));
  }

  /**
   * Szenario 9: Einfache Änderung an 1 - IN_REVIEW
   * Vorher:      |-------------|
   * Version:            1
   * <p>
   * Nachher:     |-------------|
   * Version:            1         2
   * <p>
   * Resultat:
   *  - Möglich, da Version 1 validFrom, validTo & lineType
   */
  @Test
  public void updateScenario9() {
    //given
    version1.setStatus(Status.IN_REVIEW);
    version1 = lineVersionRepository.save(version1);

    LineVersion editedVersion = version1Builder().build();
    editedVersion.setDescription("Description <changed>");
    editedVersion.setLineType(version1.getLineType());
    editedVersion.setValidFrom(version1.getValidFrom());
    editedVersion.setValidTo(version1.getValidTo());

    //when
    lineService.updateVersion(version1, editedVersion);
    List<LineVersion> result = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        version1.getSlnid());

    //then
    assertThat(result).isNotNull().hasSize(1);

    // Version 1
    LineVersion firstTemporalVersion = result.get(0);
    assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(version1.getValidFrom());
    assertThat(firstTemporalVersion.getValidTo()).isEqualTo(version1.getValidTo());
    assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.IN_REVIEW);
    assertThat(firstTemporalVersion.getDescription()).isEqualTo("Description <changed>");
  }

}