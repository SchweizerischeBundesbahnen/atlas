package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion.ServicePointVersionBuilder;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class ServicePointVersionConflictExceptionTest {

  private static ServicePointVersion version() {
    return versionBuilder().build();
  }

  private static ServicePointVersionBuilder<?, ?> versionBuilder() {
    return ServicePointVersion.builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2020, 12, 12))
        .validTo(LocalDate.of(2099, 12, 12));
  }

  @Test
  void shouldConvertToErrorMessageCorrectly() {
    // Given
    ServicePointVersionConflictException conflictException = new ServicePointVersionConflictException(
        version(), List.of(
        version(), versionBuilder().validFrom(LocalDate.of(1990, 1, 1)).build()));

    // When
    ErrorResponse errorResponse = conflictException.getErrorResponse();

    // Then
    assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(errorResponse.getMessage()).isEqualTo("A conflict occurred due to a business rule");
    assertThat(errorResponse.getDetails()).hasSize(2);

    assertThat(errorResponse.getDetails().first().getMessage()).isEqualTo(
        "Number 1234567 already taken from 01.01.1990 to 12.12.2099");
    assertThat(errorResponse.getDetails().stream().toList().get(0).getMessage()).isEqualTo(
        "Number 1234567 already taken from 01.01.1990 to 12.12.2099");
    assertThat(errorResponse.getDetails().stream().toList().get(1).getMessage()).isEqualTo(
        "Number 1234567 already taken from 12.12.2020 to 12.12.2099");
  }
}
