package ch.sbb.line.directory.module.line.exception;

import static ch.sbb.line.directory.module.line.LineVersionV3TestData.CONCESSION_MEMBER_SLNID;
import static ch.sbb.line.directory.module.line.LineVersionV3TestData.PARENT_SLNID;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class LineV3ExceptionResponseTest {

  @Test
  void shouldMapExceptionsToDocumentedHttpStatus() {
    // When & Then
    assertThat(new ReferencedLineHasParentException(PARENT_SLNID).getErrorResponse().getStatus()).isEqualTo(409);
    assertThat(new LineV3TypeNotAllowedException(LineType.TECHNICAL).getErrorResponse().getStatus()).isEqualTo(400);
    assertThat(new LineV3TypeOrderlyException(LineType.ORDERLY).getErrorResponse().getStatus()).isEqualTo(400);
    assertThat(new LineV3MemberTypeMismatchException(LineType.TEMPORARY, LineType.ORDERLY).getErrorResponse().getStatus())
        .isEqualTo(400);
    assertThat(LineV3MemberConcessionException.concessionType().getErrorResponse().getStatus()).isEqualTo(400);
    assertThat(LineV3MemberConcessionException.swissLineNumber().getErrorResponse().getStatus()).isEqualTo(400);
    assertThat(new LineV3FieldNotUpdatableException("x", "swissLineNumber", LineType.TEMPORARY).getErrorResponse().getStatus())
        .isEqualTo(409);
  }

  @Test
  void shouldReturnUnprocessableContentForDurationRules() {
    // Given
    LocalDate from = LocalDate.of(2026, 1, 1);
    LocalDate to = LocalDate.of(2026, 1, 31);

    // When & Then
    assertThat(new TemporaryLineValidationException(from, to).getErrorResponse().getStatus()).isEqualTo(422);
    assertThat(new OrderlyLineValidityException(from, to).getErrorResponse().getStatus()).isEqualTo(422);
  }

  @Test
  void shouldProduceConflictErrorResponseWithSwissLineNumberDetail() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.line();
    LineVersionV3 other = LineVersionV3TestData.lineBuilder().slnid("ch:1:slnid:1999999").build();

    // When
    ErrorResponse response = new LineV3ConflictException(line, List.of(other)).getErrorResponse();

    // Then
    assertThat(response.getStatus()).isEqualTo(409);
    assertThat(response.getDetails()).hasSize(1);
    assertThat(response.getDetails().first().getDisplayInfo().getCode()).isEqualTo("LIDI.LINE.CONFLICT.SWISS_NUMBER");
  }

  @Test
  void shouldReturnConflictWhenReferencedLineIsRevoked() {
    // When
    ErrorResponse response = new ReferencedLineRevokedException(PARENT_SLNID).getErrorResponse();

    // Then
    assertThat(response.getStatus()).isEqualTo(409);
    assertThat(response.getDetails().first().getDisplayInfo().getCode())
        .isEqualTo("LIDI.LINE.CONFLICT.REFERENCED_LINE_REVOKED");
    assertThat(response.getError()).contains(PARENT_SLNID);
  }

  @Test
  void shouldReturnConflictWhenMemberIsOutsideParentValidity() {
    // Given
    DateRange parentValidity = new DateRange(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

    // When
    ErrorResponse response = new LineV3MemberValidityException(parentValidity).getErrorResponse();

    // Then
    assertThat(response.getStatus()).isEqualTo(409);
    assertThat(response.getDetails().first().getDisplayInfo().getCode()).isEqualTo("LIDI.LINE.CONFLICT.SUBLINE_VALIDITY");
  }

  @Test
  void shouldReturnConflictListingMemberSlnidsWhenMembersAreOutsideParentValidity() {
    // Given
    DateRange parentValidity = new DateRange(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30));

    // When
    ErrorResponse response = new LineV3MembersOutsideValidityException(PARENT_SLNID, parentValidity,
        List.of(CONCESSION_MEMBER_SLNID, LineVersionV3TestData.CHILD_SLNID)).getErrorResponse();

    // Then
    assertThat(response.getStatus()).isEqualTo(409);
    assertThat(response.getDetails().first().getDisplayInfo().getCode())
        .isEqualTo("LIDI.LINE.CONFLICT.MEMBERS_OUTSIDE_VALIDITY");
    assertThat(response.getError()).contains(CONCESSION_MEMBER_SLNID, LineVersionV3TestData.CHILD_SLNID);
  }

}
