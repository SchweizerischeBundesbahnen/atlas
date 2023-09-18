package ch.sbb.line.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion.TimetableFieldNumberVersionBuilder;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

 class TimetableFieldNumberConflictExceptionTest {

  private static TimetableFieldNumberVersion version() {
    return versionBuilder().build();
  }

  private static TimetableFieldNumberVersionBuilder<?, ?> versionBuilder() {
    return TimetableFieldNumberVersion.builder()
        .ttfnid("ch:1:ttfnid:100000")
        .description("FPFN Description")
        .number("BEX")
        .swissTimetableFieldNumber("b0.BEX")
        .validFrom(LocalDate.of(2020, 12, 12))
        .validTo(LocalDate.of(2099, 12, 12));
  }

  @Test
  void shouldConvertToErrorMessageCorrectly() {
    // Given
    TimetableFieldNumberConflictException conflictException = new TimetableFieldNumberConflictException(
        version(), List.of(
        version(), versionBuilder().validFrom(LocalDate.of(1990, 1, 1)).build()));

    // When
    ErrorResponse errorResponse = conflictException.getErrorResponse();

    // Then
    assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(errorResponse.getMessage()).isEqualTo("A conflict occurred due to a business rule");
    assertThat(errorResponse.getDetails()).hasSize(4);

    assertThat(errorResponse.getDetails().first().getMessage()).isEqualTo(
        "Number BEX already taken from 01.01.1990 to 12.12.2099 by ch:1:ttfnid:100000");
    assertThat(errorResponse.getDetails().stream().toList().get(1).getMessage()).isEqualTo(
        "SwissTimetableFieldNumber b0.BEX already taken from 01.01.1990 to 12.12.2099 by ch:1:ttfnid:100000");
    assertThat(errorResponse.getDetails().stream().toList().get(2).getMessage()).isEqualTo(
        "Number BEX already taken from 12.12.2020 to 12.12.2099 by ch:1:ttfnid:100000");
    assertThat(errorResponse.getDetails().stream().toList().get(3).getMessage()).isEqualTo(
        "SwissTimetableFieldNumber b0.BEX already taken from 12.12.2020 to 12.12.2099 by ch:1:ttfnid:100000");
  }
}
