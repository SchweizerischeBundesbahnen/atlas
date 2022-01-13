package ch.sbb.timetable.field.number.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.api.ErrorResponse;
import ch.sbb.timetable.field.number.entity.Version;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ConflictExceptionTest {

  @Test
  void shouldConvertToErrorMessageCorrectly() {
    // Given
    ConflictException conflictException = new ConflictException(version(), List.of(version()));

    // When
    ErrorResponse errorResponse = conflictException.getErrorResponse();

    // Then
    assertThat(errorResponse.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(errorResponse.getMessage()).isEqualTo("A conflict occurred due to a business rule");
    assertThat(errorResponse.getDetails()).hasSize(2);

    assertThat(errorResponse.getDetails().get(0).getMessage()).isEqualTo("Number BEX already taken from 2020-12-12 to 2099-12-12 by ch:1:ttfnid:100000");
    assertThat(errorResponse.getDetails().get(1).getMessage()).isEqualTo("SwissTimetableFieldNumber b0.BEX already taken from 2020-12-12 to 2099-12-12 by ch:1:ttfnid:100000");
  }

  private static Version version() {
    return versionBuilder().build();
  }

  private static Version.VersionBuilder versionBuilder() {
    return Version.builder()
                  .ttfnid("ch:1:ttfnid:100000")
                  .name("FPFN Name")
                  .number("BEX")
                  .swissTimetableFieldNumber("b0.BEX")
                  .validFrom(LocalDate.of(2020, 12, 12))
                  .validTo(LocalDate.of(2099, 12, 12));
  }
}