package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.versioning.model.Versionable;
import java.time.LocalDate;
import java.util.List;
import java.util.SortedSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

class UpdateAffectsInReviewVersionExceptionTest {

  @Getter
  @Setter
  @AllArgsConstructor
  class AffectedVersion implements Versionable {

    private LocalDate validFrom;
    private LocalDate validTo;
    private Long id;
  }

  @Test
  void shouldGetCompleteErrorResponse() {
    // given
    UpdateAffectsInReviewVersionException exception = new UpdateAffectsInReviewVersionException(
        LocalDate.of(2020, 1, 1),
        LocalDate.of(2022, 1, 1),
        List.of(
            new AffectedVersion(LocalDate.of(2018, 1, 1), LocalDate.of(2021, 1, 1), null),
            new AffectedVersion(LocalDate.of(2022, 1, 1), LocalDate.of(2024, 1, 1), null)
        )
    );

    // when
    ErrorResponse errorResponse = exception.getErrorResponse();

    // then
    assertThat(errorResponse.getStatus()).isEqualTo(409);
    assertThat(errorResponse.getError()).isEqualTo("Update affects one or more versions that have status: IN_REVIEW.");
    assertThat(errorResponse.getMessage()).isEqualTo("Update from 01.01.2020 to 01.01.2022 affects 2 version/s that have status: "
        + "IN_REVIEW.");

    SortedSet<Detail> details = errorResponse.getDetails();
    assertThat(details).hasSize(2);
    assertThat(details.first().getMessage()).isEqualTo("Update affects version from 01.01.2018 to 01.01.2021 that has currently "
        + "the status: IN_REVIEW.");
    assertThat(details.last().getMessage()).isEqualTo("Update affects version from 01.01.2022 to 01.01.2024 that has currently "
        + "the status: IN_REVIEW.");
  }
}
