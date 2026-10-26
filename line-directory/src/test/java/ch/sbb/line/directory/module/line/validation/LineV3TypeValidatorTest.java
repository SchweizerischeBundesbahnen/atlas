package ch.sbb.line.directory.module.line.validation;

import static ch.sbb.line.directory.module.line.LineVersionV3TestData.PARENT_SLNID;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.enumaration.v3.ConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.exception.LineV3ConflictException;
import ch.sbb.line.directory.module.line.exception.LineV3MemberConcessionException;
import ch.sbb.line.directory.module.line.exception.LineV3MemberTypeMismatchException;
import ch.sbb.line.directory.module.line.exception.LineV3TypeNotAllowedException;
import ch.sbb.line.directory.module.line.exception.LineV3TypeOrderlyException;
import ch.sbb.line.directory.module.line.exception.ReferencedLineHasParentException;
import ch.sbb.line.directory.module.line.exception.ReferencedLineRevokedException;
import ch.sbb.line.directory.module.line.service.LineVersionV3ReadService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LineV3TypeValidatorTest {

  @Mock
  private LineVersionV3ReadService lineVersionV3ReadService;

  private LineV3TypeValidator typeValidator;

  @BeforeEach
  void setUp() {
    typeValidator = new LineV3TypeValidator(lineVersionV3ReadService);
  }

  private void givenParent(LineVersionV3... parentVersions) {
    when(lineVersionV3ReadService.findAllBySlnidOrderByValidFrom(PARENT_SLNID)).thenReturn(List.of(parentVersions));
  }

  @Test
  void shouldAcceptValidOrderlyLine() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.line();

    // When & Then
    assertThatCode(() -> typeValidator.validateLineType(line)).doesNotThrowAnyException();
  }

  @ParameterizedTest
  @EnumSource(value = LineType.class, names = {"TECHNICAL", "CONCESSION"})
  void shouldRejectLineWithMemberOnlyLineType(LineType lineType) {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().lineType(lineType).concessionType(null).swissLineNumber(null)
        .build();

    // When & Then
    assertThatThrownBy(() -> typeValidator.validateLineType(line))
        .isInstanceOf(LineV3TypeNotAllowedException.class);
  }

  @ParameterizedTest
  @EnumSource(value = LineType.class, names = {"DISPOSITION", "TEMPORARY", "OPERATIONAL"})
  void shouldAcceptNonOrderlyLineWithoutConcessionTypeAndSwissLineNumber(LineType lineType) {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().lineType(lineType).concessionType(null).swissLineNumber(null)
        .build();

    // When & Then
    assertThatCode(() -> typeValidator.validateLineType(line)).doesNotThrowAnyException();
  }

  @Test
  void shouldRejectOrderlyLineWithoutConcessionType() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().concessionType(null).build();

    // When & Then
    assertThatThrownBy(() -> typeValidator.validateLineType(line)).isInstanceOf(LineV3TypeOrderlyException.class);
  }

  @Test
  void shouldRejectOrderlyLineWithoutSwissLineNumber() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().swissLineNumber(null).build();

    // When & Then
    assertThatThrownBy(() -> typeValidator.validateLineType(line)).isInstanceOf(LineV3TypeOrderlyException.class);
  }

  @ParameterizedTest
  @EnumSource(value = LineType.class, names = {"DISPOSITION", "TEMPORARY", "OPERATIONAL"})
  void shouldRejectNonOrderlyLineWithSwissLineNumber(LineType lineType) {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().lineType(lineType).concessionType(null).build();

    // When & Then
    assertThatThrownBy(() -> typeValidator.validateLineType(line)).isInstanceOf(LineV3TypeOrderlyException.class);
  }

  @ParameterizedTest
  @EnumSource(value = LineType.class, names = {"DISPOSITION", "TEMPORARY", "OPERATIONAL"})
  void shouldRejectNonOrderlyLineWithConcessionType(LineType lineType) {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().lineType(lineType).swissLineNumber(null).build();

    // When & Then
    assertThatThrownBy(() -> typeValidator.validateLineType(line)).isInstanceOf(LineV3TypeOrderlyException.class);
  }

  @Test
  void shouldAcceptValidTechnicalMember() {
    // Given
    givenParent(LineVersionV3TestData.line());
    LineVersionV3 member = LineVersionV3TestData.subline();

    // When & Then
    assertThatCode(() -> typeValidator.validateMemberType(member)).doesNotThrowAnyException();
  }

  @Test
  void shouldRejectMemberWhenParentDoesNotExist() {
    // Given
    when(lineVersionV3ReadService.findAllBySlnidOrderByValidFrom(PARENT_SLNID)).thenReturn(List.of());
    LineVersionV3 member = LineVersionV3TestData.subline();

    // When & Then
    assertThatThrownBy(() -> typeValidator.validateMemberType(member)).isInstanceOf(SlnidNotFoundException.class);
  }

  @Test
  void shouldRejectMemberWhenParentIsRevoked() {
    // Given
    givenParent(LineVersionV3TestData.lineBuilder().status(Status.REVOKED).build());
    LineVersionV3 member = LineVersionV3TestData.subline();

    // When & Then
    assertThatThrownBy(() -> typeValidator.validateMemberType(member))
        .isInstanceOf(ReferencedLineRevokedException.class);
  }

  @Test
  void shouldRejectMemberWhenParentIsItselfAMember() {
    // Given
    givenParent(LineVersionV3TestData.lineBuilder().referencedLineSlnid("ch:1:slnid:1000001").build());
    LineVersionV3 member = LineVersionV3TestData.subline();

    // When & Then
    assertThatThrownBy(() -> typeValidator.validateMemberType(member))
        .isInstanceOf(ReferencedLineHasParentException.class);
  }

  @ParameterizedTest
  @CsvSource({
      "ORDERLY, CONCESSION", "ORDERLY, TECHNICAL", "DISPOSITION, DISPOSITION", "TEMPORARY, TEMPORARY",
      "OPERATIONAL, OPERATIONAL"
  })
  void shouldAcceptMemberTypeAllowedForParentType(LineType parentType, LineType memberType) {
    // Given
    givenParent(parentOfType(parentType));
    LineVersionV3 member = memberOfType(memberType);

    // When & Then
    assertThatCode(() -> typeValidator.validateMemberType(member)).doesNotThrowAnyException();
  }

  @ParameterizedTest
  @CsvSource({
      "ORDERLY, DISPOSITION", "ORDERLY, TEMPORARY", "ORDERLY, OPERATIONAL", "ORDERLY, ORDERLY",
      "DISPOSITION, TECHNICAL", "DISPOSITION, CONCESSION", "TEMPORARY, DISPOSITION", "TEMPORARY, TECHNICAL",
      "OPERATIONAL, CONCESSION", "OPERATIONAL, TEMPORARY"
  })
  void shouldRejectMemberTypeNotAllowedForParentType(LineType parentType, LineType memberType) {
    // Given
    givenParent(parentOfType(parentType));
    LineVersionV3 member = memberOfType(memberType);

    // When & Then
    assertThatThrownBy(() -> typeValidator.validateMemberType(member))
        .isInstanceOf(LineV3MemberTypeMismatchException.class);
  }

  @Test
  void shouldRejectConcessionMemberWithoutConcessionType() {
    // Given
    givenParent(LineVersionV3TestData.line());
    LineVersionV3 member = LineVersionV3TestData.concessionMemberBuilder().concessionType(null).build();

    // When & Then
    assertThatThrownBy(() -> typeValidator.validateMemberType(member))
        .isInstanceOf(LineV3MemberConcessionException.class);
  }

  @Test
  void shouldRejectConcessionMemberWithoutSwissLineNumber() {
    // Given
    givenParent(LineVersionV3TestData.line());
    LineVersionV3 member = LineVersionV3TestData.concessionMemberBuilder().swissLineNumber(null).build();

    // When & Then
    assertThatThrownBy(() -> typeValidator.validateMemberType(member))
        .isInstanceOf(LineV3MemberConcessionException.class);
  }

  @Test
  void shouldRejectTechnicalMemberWithConcessionType() {
    // Given
    givenParent(LineVersionV3TestData.line());
    LineVersionV3 member = LineVersionV3TestData.sublineBuilder().concessionType(ConcessionType.CANTONALLY_APPROVED_LINE)
        .build();

    // When & Then
    assertThatThrownBy(() -> typeValidator.validateMemberType(member))
        .isInstanceOf(LineV3MemberConcessionException.class);
  }

  @Test
  void shouldRejectTechnicalMemberWithSwissLineNumber() {
    // Given
    givenParent(LineVersionV3TestData.line());
    LineVersionV3 member = LineVersionV3TestData.sublineBuilder().swissLineNumber("90.583.2").build();

    // When & Then
    assertThatThrownBy(() -> typeValidator.validateMemberType(member))
        .isInstanceOf(LineV3MemberConcessionException.class);
  }

  @Test
  void shouldAcceptConcessionMemberWithConcessionTypeAndSwissLineNumber() {
    // Given
    givenParent(LineVersionV3TestData.line());
    LineVersionV3 member = LineVersionV3TestData.concessionMemberBuilder().build();

    // When & Then
    assertThatCode(() -> typeValidator.validateMemberType(member)).doesNotThrowAnyException();
  }

  @Test
  void shouldRejectOrderlyLineWhenSwissLineNumberOverlapsWithOtherSlnid() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.line();
    LineVersionV3 other = LineVersionV3TestData.lineBuilder().slnid("ch:1:slnid:1999999").build();
    when(lineVersionV3ReadService.findSwissLineNumberOverlaps(line)).thenReturn(List.of(other));

    // When & Then
    assertThatThrownBy(() -> typeValidator.validateSwissLineNumberUniqueness(line))
        .isInstanceOf(LineV3ConflictException.class);
  }

  @Test
  void shouldRejectConcessionMemberWhenSwissLineNumberOverlapsWithOtherSlnid() {
    // Given
    LineVersionV3 member = LineVersionV3TestData.concessionMemberBuilder().build();
    LineVersionV3 other = LineVersionV3TestData.lineBuilder().slnid("ch:1:slnid:1999999").swissLineNumber("90.583.1").build();
    when(lineVersionV3ReadService.findSwissLineNumberOverlaps(member)).thenReturn(List.of(other));

    // When & Then
    assertThatThrownBy(() -> typeValidator.validateSwissLineNumberUniqueness(member))
        .isInstanceOf(LineV3ConflictException.class);
  }

  @Test
  void shouldNotCheckSwissLineNumberOverlapForNonOrderlyLine() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().lineType(LineType.TEMPORARY).concessionType(null)
        .swissLineNumber(null).build();
    when(lineVersionV3ReadService.findSwissLineNumberOverlaps(any())).thenReturn(List.of(line));

    // When & Then
    assertThatCode(() -> typeValidator.validateSwissLineNumberUniqueness(line)).doesNotThrowAnyException();
  }

  private static LineVersionV3 parentOfType(LineType parentType) {
    var builder = LineVersionV3TestData.lineBuilder().lineType(parentType);
    if (parentType != LineType.ORDERLY) {
      builder.concessionType(null).swissLineNumber(null);
    }
    return builder.build();
  }

  private static LineVersionV3 memberOfType(LineType memberType) {
    var builder = LineVersionV3TestData.sublineBuilder().lineType(memberType);
    if (memberType == LineType.CONCESSION) {
      builder.concessionType(ConcessionType.CANTONALLY_APPROVED_LINE).swissLineNumber("90.583.1");
    }
    return builder.build();
  }

}
