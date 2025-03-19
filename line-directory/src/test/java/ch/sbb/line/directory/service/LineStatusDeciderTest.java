package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class LineStatusDeciderTest {

  private final LineStatusDecider lineStatusDecider = new LineStatusDecider();

  @Test
  void shouldSetStatusToDraftOnCreateOrderly() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().lineType(LineType.ORDERLY).build();
    // When
    Status result = lineStatusDecider.getStatusForLine(lineVersion, Optional.empty(), Collections.emptyList());
    // Then
    assertThat(result).isEqualTo(Status.DRAFT);
  }

  @Test
  void shouldSetStatusToValidatedOnCreateTemporary() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().lineType(LineType.TEMPORARY).build();
    // When
    Status result = lineStatusDecider.getStatusForLine(lineVersion, Optional.empty(), Collections.emptyList());
    // Then
    assertThat(result).isEqualTo(Status.VALIDATED);
  }

  @Test
  void shouldSetStatusToDraftOnUpdateOrderlyShortNumberChange() {
    // Given
    LineVersion currentLineVersion =
        LineTestData.lineVersionBuilder().shortNumber("curr num").build();
    LineVersion newLineVersion = LineTestData.lineVersionBuilder().longName("new num").build();
    // When
    Status result = lineStatusDecider.getStatusForLine(newLineVersion, Optional.of(currentLineVersion),
        List.of(currentLineVersion));
    // Then
    assertThat(result).isEqualTo(Status.DRAFT);
  }

  @Test
  void shouldSetStatusToDraftOnUpdateOrderlyShortNumberChangeFromNull() {
    // Given
    LineVersion currentLineVersion =
        LineTestData.lineVersionBuilder().shortNumber(null).build();
    LineVersion newLineVersion = LineTestData.lineVersionBuilder().shortNumber("new num").build();
    // When
    Status result = lineStatusDecider.getStatusForLine(newLineVersion, Optional.of(currentLineVersion),
        List.of(currentLineVersion));
    // Then
    assertThat(result).isEqualTo(Status.DRAFT);
  }

  @Test
  void shouldSetStatusToDraftOnUpdateOrderlyShortNumberChangeToNull() {
    // Given
    LineVersion currentLineVersion =
        LineTestData.lineVersionBuilder().shortNumber("something").build();
    LineVersion newLineVersion = LineTestData.lineVersionBuilder().shortNumber(null).build();
    // When
    Status result = lineStatusDecider.getStatusForLine(newLineVersion, Optional.of(currentLineVersion),
        List.of(currentLineVersion));
    // Then
    assertThat(result).isEqualTo(Status.DRAFT);
  }

  @Test
  void shouldSetStatusToDraftOnUpdateTemporaryNameChange() {
    // Given
    LineVersion currentLineVersion =
        LineTestData.lineVersionBuilder().longName("current name").build();
    LineVersion newLineVersion = LineTestData.lineVersionBuilder().lineType(LineType.TEMPORARY).longName("new name").build();
    // When
    Status result = lineStatusDecider.getStatusForLine(newLineVersion, Optional.of(currentLineVersion),
        List.of(currentLineVersion));
    // Then
    assertThat(result).isEqualTo(Status.VALIDATED);
  }

  @Test
  void shouldSetStatusToDraftOnUpdateOrderlyProlongValidToChange() {
    // Given
    LineVersion currentLineVersion =
        LineTestData.lineVersionBuilder().validTo(LocalDate.of(2020, 12, 31)).build();
    LineVersion newLineVersion = LineTestData.lineVersionBuilder().validTo(LocalDate.of(2025, 12, 31)).build();
    // When
    Status result = lineStatusDecider.getStatusForLine(newLineVersion, Optional.of(currentLineVersion),
        List.of(currentLineVersion));
    // Then
    assertThat(result).isEqualTo(Status.DRAFT);
  }

  @Test
  void shouldSetStatusToDraftOnUpdateOrderlyProlongValidFromChange() {
    // Given
    LineVersion currentLineVersion =
        LineTestData.lineVersionBuilder().validFrom(LocalDate.of(2020, 1, 1)).build();
    LineVersion newLineVersion = LineTestData.lineVersionBuilder().validFrom(LocalDate.of(2018, 1, 1)).build();
    // When
    Status result = lineStatusDecider.getStatusForLine(newLineVersion, Optional.of(currentLineVersion),
        List.of(currentLineVersion));
    // Then
    assertThat(result).isEqualTo(Status.DRAFT);
  }

  @Test
  void shouldSetStatusToValidatedOnUpdateOrderlyShortenValidTo() {
    // Given
    LineVersion currentLineVersion =
        LineTestData.lineVersionBuilder().validTo(LocalDate.of(2020, 12, 31)).build();
    LineVersion newLineVersion = LineTestData.lineVersionBuilder().validTo(LocalDate.of(2020, 11, 30)).build();
    // When
    Status result = lineStatusDecider.getStatusForLine(newLineVersion, Optional.of(currentLineVersion),
        List.of(currentLineVersion));
    // Then
    assertThat(result).isEqualTo(Status.VALIDATED);
  }

  @Test
  void shouldSetStatusToValidatedOnUpdateOrderlyShortenValidFrom() {
    // Given
    LineVersion currentLineVersion =
        LineTestData.lineVersionBuilder().validFrom(LocalDate.of(2020, 1, 1)).build();
    LineVersion newLineVersion = LineTestData.lineVersionBuilder().validFrom(LocalDate.of(2020, 2, 1)).build();
    // When
    Status result = lineStatusDecider.getStatusForLine(newLineVersion, Optional.of(currentLineVersion),
        List.of(currentLineVersion));
    // Then
    assertThat(result).isEqualTo(Status.VALIDATED);
  }

  @Test
  void shouldSetStatusToInReviewIfItIsAlreadyInReview() {
    // Given
    LineVersion currentLineVersion =
        LineTestData.lineVersionBuilder().id(1L).status(Status.IN_REVIEW).build();
    LineVersion newLineVersion = LineTestData.lineVersionBuilder().id(1L).description("Other description").build();
    // When
    Status result = lineStatusDecider.getStatusForLine(newLineVersion, Optional.of(currentLineVersion),
        List.of(currentLineVersion));
    // Then
    assertThat(result).isEqualTo(Status.IN_REVIEW);
  }

  @Test
  void shouldSwitchToDraftStatusWhenChangedFromTemporaryToOrderly() {
    // Given
    LineVersion currentLineVersion = LineTestData.lineVersionBuilder().lineType(LineType.TEMPORARY).build();
    LineVersion newLineVersion = LineTestData.lineVersionBuilder().lineType(LineType.ORDERLY).build();
    // When
    Status result = lineStatusDecider.getStatusForLine(newLineVersion, Optional.of(currentLineVersion),
        List.of(currentLineVersion));
    // Then
    assertThat(result).isEqualTo(Status.DRAFT);
  }

  @Test
  void shouldSwitchToDraftStatusWhenChangedFromOperationalToOrderly() {
    // Given
    LineVersion currentLineVersion = LineTestData.lineVersionBuilder().lineType(LineType.OPERATIONAL).build();
    LineVersion newLineVersion = LineTestData.lineVersionBuilder().lineType(LineType.ORDERLY).build();
    // When
    Status result = lineStatusDecider.getStatusForLine(newLineVersion, Optional.of(currentLineVersion),
        List.of(currentLineVersion));
    // Then
    assertThat(result).isEqualTo(Status.DRAFT);
  }

  @Test
  void shouldSwitchToValidatedStatusWhenChangedFromOrderlyToOperational() {
    // Given
    LineVersion currentLineVersion = LineTestData.lineVersionBuilder().lineType(LineType.ORDERLY).status(Status.DRAFT).build();
    LineVersion newLineVersion = LineTestData.lineVersionBuilder().lineType(LineType.OPERATIONAL).build();
    // When
    Status result = lineStatusDecider.getStatusForLine(newLineVersion, Optional.of(currentLineVersion),
        List.of(currentLineVersion));
    // Then
    assertThat(result).isEqualTo(Status.VALIDATED);
  }

  @Test
  void shouldSetStatusToDraftOnOfferCategoryUpdate() {
    // Given
    LineVersion currentLineVersion =
        LineTestData.lineVersionBuilder().offerCategory(OfferCategory.IC).build();
    LineVersion newLineVersion = LineTestData.lineVersionBuilder().offerCategory(OfferCategory.B).build();
    // When
    Status result = lineStatusDecider.getStatusForLine(newLineVersion, Optional.of(currentLineVersion),
        List.of(currentLineVersion));
    // Then
    assertThat(result).isEqualTo(Status.DRAFT);
  }

  @Test
  void shouldSetStatusToDraftOnConcessionTypeUpdate() {
    // Given
    LineVersion currentLineVersion =
        LineTestData.lineVersionBuilder().concessionType(LineConcessionType.CANTONALLY_APPROVED_LINE).build();
    LineVersion newLineVersion = LineTestData.lineVersionBuilder().concessionType(LineConcessionType.LINE_OF_A_ZONE_CONCESSION)
        .build();
    // When
    Status result = lineStatusDecider.getStatusForLine(newLineVersion, Optional.of(currentLineVersion),
        List.of(currentLineVersion));
    // Then
    assertThat(result).isEqualTo(Status.DRAFT);
  }

  @Test
  void shouldSetStatusToValidatedOnBusinessOrganisationUpdate() {
    // Given
    LineVersion currentLineVersion =
        LineTestData.lineVersionBuilder().businessOrganisation("sboid").build();
    LineVersion newLineVersion =
        LineTestData.lineVersionBuilder().businessOrganisation("newsboid").build();
    // When
    Status result = lineStatusDecider.getStatusForLine(newLineVersion, Optional.of(currentLineVersion),
        List.of(currentLineVersion));
    // Then
    assertThat(result).isEqualTo(Status.VALIDATED);
  }
}