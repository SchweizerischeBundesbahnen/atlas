package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.line.directory.IntegrationTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.exception.ConflictExcpetion;
import ch.sbb.line.directory.model.SearchRestrictions;
import ch.sbb.line.directory.repository.LineVersionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

@IntegrationTest
class LineServiceTest {

  private final LineVersionRepository lineVersionRepository;
  private final LineService lineService;

  @Autowired
  public LineServiceTest(
      LineVersionRepository lineVersionRepository,
      LineService lineService) {
    this.lineVersionRepository = lineVersionRepository;
    this.lineService = lineService;
  }

  @Test
  void shouldGetPagableLinesFromRepository() {
    // Given
    Pageable pageable = Pageable.unpaged();

    // When
    Page<Line> lines = lineService.findAll(SearchRestrictions.<LineType>builder().pageable(pageable).build());

    // Then
    assertThat(lines.getTotalElements()).isEqualTo(0);
  }

  @Test
  void shouldGetLineVersions() {
    // Given
    String slnid = "slnid";

    // When
    List<LineVersion> lineVersions = lineService.findLineVersions(slnid);

    // Then
    assertThat(lineVersions.size()).isEqualTo(0);
  }

  @Test
  void shouldGetLine() {
    // Given
    String slnid = "slnid";

    // When
    Optional<Line> line = lineService.findLine(slnid);

    // Then
    assertThat(line.isEmpty()).isTrue();
  }

  @Test
  void shouldGetLineFromRepository() {
    // Given
    LineVersion saved = lineVersionRepository.save(LineTestData.lineVersionBuilder().build());

    // When
    Optional<LineVersion> result = lineService.findById(saved.getId());

    // Then
    assertThat(result).isPresent();
  }

  @Test
  void shouldSaveLineWithValidation() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersion();

    // When
    LineVersion result = lineService.save(lineVersion);

    // Then
    assertThat(result).isEqualTo(lineVersion);
  }

  @Test
  void shouldDeleteLinesWhenNotFound() {
    // Given
    String slnid = "ch:1:ttfnid:1000083";

    //When & Then
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
        () -> lineService.deleteAll(slnid));
  }

  @Test
  void shouldDeleteLines() {
    // Given
    LineVersion saved = lineVersionRepository.save(LineTestData.lineVersion());
    String slnid = saved.getSlnid();

    //When
    lineService.deleteAll(slnid);

    //Then
    assertThat(lineVersionRepository.findAllBySlnidOrderByValidFrom(slnid).size()).isEqualTo(0);
  }

  @Test
  void shouldDeleteLine() {
    // Given
    LineVersion saved = lineVersionRepository.save(LineTestData.lineVersion());
    Long id = saved.getId();

    // When
    lineService.deleteById(id);

    // Then
    assertThat(lineVersionRepository.findById(id)).isEmpty();
  }

  @Test
  void shouldNotDeleteLineWhenNotFound() {
    // When
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
        () -> lineService.deleteById(1L));
  }

  @Test
  void shouldNotSaveTemporaryLineWithValidityGreaterThan12Months() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 10, 1))
        .validTo(LocalDate.of(2022, 11, 2)).build();
    // When
    assertThatExceptionOfType(ConflictExcpetion.class).isThrownBy(() -> lineService.save(lineVersion));
  }

  @Test
  void shouldSaveTemporaryLine() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 10, 1))
        .validTo(LocalDate.of(2022, 9, 1)).build();
    // When
    LineVersion saved = lineService.save(lineVersion);
    // Then
    assertThat(saved).usingRecursiveComparison().isEqualTo(lineVersion);
  }

  @Test
  void shouldNotSaveWith2ExistentTemporaryVersionsWhichAffectIncomingVersionWhenValidityLongerThan12() {
    // Given
    LineVersion lineVersion1 = lineVersionRepository.save(LineTestData.lineVersionBuilder().type(LineType.TEMPORARY)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 3, 31)).build());
    lineVersionRepository.save(LineTestData.lineVersionBuilder().type(LineType.TEMPORARY)
        .validFrom(LocalDate.of(2021, 9, 1))
        .validTo(LocalDate.of(2022, 2, 1)).slnid(lineVersion1.getSlnid()).build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2021, 8, 31)).slnid(lineVersion1.getSlnid()).build();
    // When
    assertThatExceptionOfType(ConflictExcpetion.class).isThrownBy(() -> lineService.save(lineVersion));
  }

  @Test
  void shouldSaveTemporaryLineWith1ExistentTemporaryVersionWhichAffectsWhenValidityIsLessThan12() {
    // Given
    LineVersion lineVersion1 = lineVersionRepository.save(LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 3, 31)).build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2021, 7, 31)).slnid(lineVersion1.getSlnid()).build();
    // When
    LineVersion saved = lineService.save(lineVersion);
    // Then
    assertThat(saved).usingRecursiveComparison().isEqualTo(lineVersion);
  }

  @Test
  void shouldThrowExceptionOn2TemporaryNew1NotTemporary() {
    // Given
    LineVersion lineVersion1 = lineVersionRepository.save(LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 3, 31)).build());
    lineVersionRepository.save(LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2021, 8, 31)).slnid(lineVersion1.getSlnid()).build());
    lineVersionRepository.save(LineTestData.lineVersionBuilder().validFrom(LocalDate.of(2022, 3, 1))
        .validTo(LocalDate.of(2022, 3, 31)).slnid(lineVersion1.getSlnid()).build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 9, 1))
        .validTo(LocalDate.of(2022, 2, 28)).slnid(lineVersion1.getSlnid()).build();
    // When
    assertThatExceptionOfType(ConflictExcpetion.class).isThrownBy(() -> lineService.save(lineVersion));
  }

  @Test
  void shouldSaveOn2TemporaryNew1NotTemporary() {
    // Given
    LineVersion lineVersion1 = lineVersionRepository.save(LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 3, 31)).build());
    lineVersionRepository.save(LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2021, 8, 31)).slnid(lineVersion1.getSlnid()).build());
    lineVersionRepository.save(LineTestData.lineVersionBuilder().validFrom(LocalDate.of(2021, 10, 1))
        .validTo(LocalDate.of(2022, 3, 31)).slnid(lineVersion1.getSlnid()).build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 9, 1))
        .validTo(LocalDate.of(2021, 9, 30)).slnid(lineVersion1.getSlnid()).build();
    // When
    LineVersion saved = lineService.save(lineVersion);
    // Then
    assertThat(saved).usingRecursiveComparison().isEqualTo(lineVersion);
  }

  @Test
  void shouldWorkOn1TemporaryNewWithGap() {
    // Given
    LineVersion lineVersion1 = lineVersionRepository.save(LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 3, 31)).build());
    LineVersion lineVersion = LineTestData.lineVersionBuilder().type(LineType.TEMPORARY).validFrom(LocalDate.of(2021, 4, 2))
        .validTo(LocalDate.of(2021, 8, 31)).slnid(lineVersion1.getSlnid()).build();
    // When
    LineVersion saved = lineService.save(lineVersion);
    // Then
    assertThat(saved).usingRecursiveComparison().isEqualTo(lineVersion);
  }

  @AfterEach
  void clanupDb() {
    lineVersionRepository.deleteAll();
  }
}
