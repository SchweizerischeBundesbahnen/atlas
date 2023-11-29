package ch.sbb.line.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse.Parameter;
import ch.sbb.line.directory.LineTestData;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;

 class TemporaryLineValidationExceptionTest {

  @Test
  void testGetErrorResponseWithMultipleRelatingVersions() {
    try {
      throw new TemporaryLineValidationException(
          List.of(
              LineTestData.lineVersionBuilder()
                  .validFrom(LocalDate.of(2021, 10, 2))
                  .validTo(LocalDate.of(2022, 2, 1))
                  .build(),
              LineTestData.lineVersionBuilder()
                  .validFrom(LocalDate.of(2021, 1, 1))
                  .validTo(LocalDate.of(2021, 5, 1))
                  .build(),
              LineTestData.lineVersionBuilder()
                  .validFrom(LocalDate.of(2021, 5, 2))
                  .validTo(LocalDate.of(2021, 10, 1))
                  .build()
          )
      );
    } catch (TemporaryLineValidationException exception) {
      assertThat(exception.getErrorResponse().getStatus()).isEqualTo(422);
      assertThat(exception.getErrorResponse().getMessage()).isEqualTo(
          "Business rule validation failed");
      assertThat(exception.getErrorResponse().getDetails()).hasSize(3);
      assertThat(exception.getErrorResponse().getDetails().first().getField()).isEqualTo("validTo");
      assertThat(exception.getErrorResponse().getDetails().first().getMessage()).isEqualTo(
          "Temporary version from 01.01.2021 to 01.05.2021 is a part of relating temporary versions, which together exceed maximum validity of 12 months");
      assertThat(
          exception.getErrorResponse().getDetails().first().getDisplayInfo().getCode()).isEqualTo(
          "LIDI.LINE.RELATING_TEMPORARY_VERSIONS_EXCEED_MAX_VALIDITY");
      assertThat(exception.getErrorResponse()
          .getDetails()
          .first()
          .getDisplayInfo()
          .getParameters()).usingRecursiveComparison().isEqualTo(
          List.of(
              new Parameter("validFrom",
                  LocalDate.of(2021, 1, 1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))),
              new Parameter("validTo",
                  LocalDate.of(2021, 5, 1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
          )
      );
    }
  }

  @Test
  void testGetErrorResponseWithOneVersion() {
    try {
      throw new TemporaryLineValidationException(
          List.of(
              LineTestData.lineVersionBuilder().validFrom(LocalDate.of(2021, 1, 1))
                  .validTo(LocalDate.of(2022, 5, 1)).build()
          )
      );
    } catch (TemporaryLineValidationException exception) {
      assertThat(exception.getErrorResponse().getStatus()).isEqualTo(422);
      assertThat(exception.getErrorResponse().getMessage()).isEqualTo(
          "Business rule validation failed");
      assertThat(exception.getErrorResponse().getDetails()).hasSize(1);
      assertThat(exception.getErrorResponse().getDetails().first().getField()).isEqualTo("validTo");
      assertThat(exception.getErrorResponse().getDetails().first().getMessage()).isEqualTo(
          "Temporary version from 01.01.2021 to 01.05.2022 exceeds maximum validity of 12 months");
      assertThat(
          exception.getErrorResponse().getDetails().first().getDisplayInfo().getCode()).isEqualTo(
          "LIDI.LINE.TEMPORARY_VERSION_EXCEEDS_MAX_VALIDITY");
      assertThat(exception.getErrorResponse()
          .getDetails()
          .first()
          .getDisplayInfo()
          .getParameters()).usingRecursiveComparison().isEqualTo(
          List.of(
              new Parameter("validFrom",
                  LocalDate.of(2021, 1, 1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))),
              new Parameter("validTo",
                  LocalDate.of(2022, 5, 1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
          )
      );
    }
  }

}
