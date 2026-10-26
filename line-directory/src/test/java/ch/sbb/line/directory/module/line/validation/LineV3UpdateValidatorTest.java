package ch.sbb.line.directory.module.line.validation;

import static ch.sbb.line.directory.module.line.LineVersionV3TestData.CONCESSION_MEMBER_SLNID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.atlas.api.lidi.enumaration.v3.ConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.exception.RevokedException;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.exception.LineV3FieldNotUpdatableException;
import org.junit.jupiter.api.Test;

class LineV3UpdateValidatorTest {

  private final LineV3UpdateValidator updateValidator = new LineV3UpdateValidator();

  @Test
  void shouldRejectUpdateOfRevokedLine() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.lineBuilder().status(Status.REVOKED).build();

    // When & Then
    assertThatThrownBy(() -> updateValidator.validateNotRevoked(current)).isInstanceOf(RevokedException.class);
  }

  @Test
  void shouldRejectUpdateOfRevokedMember() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.sublineBuilder().status(Status.REVOKED).build();

    // When & Then
    assertThatThrownBy(() -> updateValidator.validateNotRevoked(current)).isInstanceOf(RevokedException.class);
  }

  @Test
  void shouldAcceptUpdateOfValidatedLine() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.line();

    // When & Then
    assertThatCode(() -> updateValidator.validateNotRevoked(current)).doesNotThrowAnyException();
  }

  @Test
  void shouldRejectUpdatingSwissLineNumberOfNonOrderlyLine() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.lineBuilder().lineType(LineType.TEMPORARY).concessionType(null)
        .swissLineNumber(null).build();
    LineVersionV3 edited = current.toBuilder().swissLineNumber("90.1").build();

    // When & Then
    assertThatThrownBy(() -> updateValidator.validateConcessionFieldsUpdatable(current, edited))
        .isInstanceOf(LineV3FieldNotUpdatableException.class);
  }

  @Test
  void shouldRejectUpdatingConcessionTypeOfNonOrderlyLine() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.lineBuilder().lineType(LineType.DISPOSITION).concessionType(null)
        .swissLineNumber(null).build();
    LineVersionV3 edited = current.toBuilder().concessionType(ConcessionType.CANTONALLY_APPROVED_LINE).build();

    // When & Then
    assertThatThrownBy(() -> updateValidator.validateConcessionFieldsUpdatable(current, edited))
        .isInstanceOf(LineV3FieldNotUpdatableException.class);
  }

  @Test
  void shouldAcceptUpdateOfNonOrderlyLineWithoutSwissLineNumberAndConcessionType() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.lineBuilder().lineType(LineType.OPERATIONAL).concessionType(null)
        .swissLineNumber(null).build();
    LineVersionV3 edited = current.toBuilder().description("changed").build();

    // When & Then
    assertThatCode(() -> updateValidator.validateConcessionFieldsUpdatable(current, edited)).doesNotThrowAnyException();
  }

  @Test
  void shouldAcceptUpdateOfSwissLineNumberOfOrderlyLine() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.line();
    LineVersionV3 edited = current.toBuilder().swissLineNumber("90.999").build();

    // When & Then
    assertThatCode(() -> updateValidator.validateConcessionFieldsUpdatable(current, edited)).doesNotThrowAnyException();
  }

  @Test
  void shouldAcceptUpdateOfConcessionMemberSwissLineNumber() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.concessionMemberBuilder().build();
    LineVersionV3 edited = current.toBuilder().swissLineNumber("90.583.9").build();

    // When & Then
    assertThat(current.getSlnid()).isEqualTo(CONCESSION_MEMBER_SLNID);
    assertThatCode(() -> updateValidator.validateConcessionFieldsUpdatable(current, edited)).doesNotThrowAnyException();
  }

}
