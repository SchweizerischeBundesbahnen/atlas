package ch.sbb.line.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.api.ErrorResponse.Parameter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TemporaryLineValidationExceptionTest {

  @Test
  void testGetErrorResponseWithMultipleRelatingVersions() {
    try {
      throw new TemporaryLineValidationException(
          List.of(
              LineTestData.lineVersionBuilder().validFrom(LocalDate.of(2021, 1, 1)).validTo(LocalDate.of(2021, 5, 1)).build(),
              LineTestData.lineVersionBuilder().validFrom(LocalDate.of(2021, 5, 2)).validTo(LocalDate.of(2021, 10, 1)).build(),
              LineTestData.lineVersionBuilder().validFrom(LocalDate.of(2021, 10, 2)).validTo(LocalDate.of(2022, 2, 1)).build()
          )
      );
    } catch (TemporaryLineValidationException exception) {
      assertThat(exception.getErrorResponse().getHttpStatus()).isEqualTo(422);
      assertThat(exception.getErrorResponse().getMessage()).isEqualTo("Business rule validation failed");
      assertThat(exception.getErrorResponse().getDetails().size()).isEqualTo(3);
      assertThat(exception.getErrorResponse().getDetails().get(0).getField()).isEqualTo("validTo");
      assertThat(exception.getErrorResponse().getDetails().get(0).getMessage()).isEqualTo(
          "Temporary version from 2021-01-01 to 2021-05-01 is a part of relating temporary versions, which together exceed maximum validity of 12 months");
      assertThat(exception.getErrorResponse().getDetails().get(0).getDisplayInfo().getCode()).isEqualTo("LIDI.LINE.TEMPORARY_VERSION_EXCEEDS_MAX_VALIDITY");
      assertThat(exception.getErrorResponse().getDetails().get(0).getDisplayInfo().getParameters()).usingRecursiveComparison().isEqualTo(
          List.of(
              new Parameter("validFrom", LocalDate.of(2021, 1, 1).format(DateTimeFormatter.ISO_DATE)),
              new Parameter("validTo", LocalDate.of(2021, 5, 1).format(DateTimeFormatter.ISO_DATE))
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
      assertThat(exception.getErrorResponse().getHttpStatus()).isEqualTo(422);
      assertThat(exception.getErrorResponse().getMessage()).isEqualTo("Business rule validation failed");
      assertThat(exception.getErrorResponse().getDetails().size()).isEqualTo(1);
      assertThat(exception.getErrorResponse().getDetails().get(0).getField()).isEqualTo("validTo");
      assertThat(exception.getErrorResponse().getDetails().get(0).getMessage()).isEqualTo(
          "Temporary version from 2021-01-01 to 2022-05-01 exceeds maximum validity of 12 months");
      assertThat(exception.getErrorResponse().getDetails().get(0).getDisplayInfo().getCode()).isEqualTo("LIDI.LINE.TEMPORARY_VERSION_EXCEEDS_MAX_VALIDITY");
      assertThat(exception.getErrorResponse().getDetails().get(0).getDisplayInfo().getParameters()).usingRecursiveComparison().isEqualTo(
          List.of(
              new Parameter("validFrom", LocalDate.of(2021, 1, 1).format(DateTimeFormatter.ISO_DATE)),
              new Parameter("validTo", LocalDate.of(2022, 5, 1).format(DateTimeFormatter.ISO_DATE))
          )
      );
    }
  }

}
