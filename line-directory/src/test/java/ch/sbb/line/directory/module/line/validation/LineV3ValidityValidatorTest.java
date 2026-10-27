package ch.sbb.line.directory.module.line.validation;

import static ch.sbb.line.directory.module.line.LineVersionV3TestData.CONCESSION_MEMBER_SLNID;
import static ch.sbb.line.directory.module.line.LineVersionV3TestData.PARENT_SLNID;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.exception.LineV3MemberValidityException;
import ch.sbb.line.directory.module.line.exception.LineV3MembersOutsideValidityException;
import ch.sbb.line.directory.module.line.exception.OrderlyLineValidityException;
import ch.sbb.line.directory.module.line.exception.TemporaryLineValidationException;
import ch.sbb.line.directory.module.line.service.LineVersionV3ReadService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LineV3ValidityValidatorTest {

  @Mock
  private LineVersionV3ReadService lineVersionV3ReadService;

  private LineV3ValidityValidator validityValidator;

  @BeforeEach
  void setUp() {
    validityValidator = new LineV3ValidityValidator(lineVersionV3ReadService);
  }

  private void givenParent(LineVersionV3... parentVersions) {
    when(lineVersionV3ReadService.findAllBySlnidOrderByValidFrom(PARENT_SLNID)).thenReturn(List.of(parentVersions));
  }

  @Test
  void shouldRejectTemporaryLineWithTotalValidityOver14Days() {
    // Given
    LineVersionV3 first = temporaryLine(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10));
    LineVersionV3 second = temporaryLine(LocalDate.of(2026, 1, 11), LocalDate.of(2026, 1, 15));

    // When & Then
    assertThatThrownBy(() -> validityValidator.validateLineTypeDurationLimits(List.of(first, second)))
        .isInstanceOf(TemporaryLineValidationException.class);
  }

  @Test
  void shouldAcceptTemporaryLineWithTotalValidityOf14DaysAcrossVersions() {
    // Given
    LineVersionV3 first = temporaryLine(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10));
    LineVersionV3 second = temporaryLine(LocalDate.of(2026, 1, 11), LocalDate.of(2026, 1, 14));

    // When & Then
    assertThatCode(() -> validityValidator.validateLineTypeDurationLimits(List.of(first, second)))
        .doesNotThrowAnyException();
  }

  @Test
  void shouldRejectOrderlyLineWithTotalValidityUnder15Days() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(2026, 1, 14)).build();

    // When & Then
    assertThatThrownBy(() -> validityValidator.validateLineTypeDurationLimits(List.of(line)))
        .isInstanceOf(OrderlyLineValidityException.class);
  }

  @Test
  void shouldAcceptOrderlyLineWithTotalValidityOf15DaysAcrossVersions() {
    // Given
    LineVersionV3 first = LineVersionV3TestData.lineBuilder().validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(2026, 1, 7)).build();
    LineVersionV3 second = LineVersionV3TestData.lineBuilder().validFrom(LocalDate.of(2026, 1, 8))
        .validTo(LocalDate.of(2026, 1, 15)).build();

    // When & Then
    assertThatCode(() -> validityValidator.validateLineTypeDurationLimits(List.of(first, second)))
        .doesNotThrowAnyException();
  }

  @Test
  void shouldNotLimitDurationOfOtherLineTypes() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().lineType(LineType.OPERATIONAL).concessionType(null)
        .swissLineNumber(null).validFrom(LocalDate.of(2026, 1, 1)).validTo(LocalDate.of(2026, 1, 2)).build();

    // When & Then
    assertThatCode(() -> validityValidator.validateLineTypeDurationLimits(List.of(line))).doesNotThrowAnyException();
  }

  @Test
  void shouldAcceptMemberWithinParentOverallValidity() {
    // Given
    givenParent(
        LineVersionV3TestData.lineBuilder().validFrom(LocalDate.of(2026, 1, 1)).validTo(LocalDate.of(2026, 6, 30)).build(),
        LineVersionV3TestData.lineBuilder().validFrom(LocalDate.of(2026, 7, 1)).validTo(LocalDate.of(2026, 12, 31)).build());
    LineVersionV3 member = LineVersionV3TestData.sublineBuilder().validFrom(LocalDate.of(2026, 3, 1))
        .validTo(LocalDate.of(2026, 12, 31)).build();

    // When & Then
    assertThatCode(() -> validityValidator.validateMemberWithinReferencedLineValidity(List.of(member)))
        .doesNotThrowAnyException();
  }

  @Test
  void shouldRejectMemberStartingBeforeParentValidity() {
    // Given
    givenParent(LineVersionV3TestData.line());
    LineVersionV3 member = LineVersionV3TestData.sublineBuilder().validFrom(LocalDate.of(2025, 12, 31)).build();

    // When & Then
    assertThatThrownBy(() -> validityValidator.validateMemberWithinReferencedLineValidity(List.of(member)))
        .isInstanceOf(LineV3MemberValidityException.class);
  }

  @Test
  void shouldRejectMemberEndingAfterParentValidity() {
    // Given
    givenParent(LineVersionV3TestData.lineBuilder().validTo(LocalDate.of(2026, 12, 31)).build());
    LineVersionV3 member = LineVersionV3TestData.sublineBuilder().validTo(LocalDate.of(2027, 1, 1)).build();

    // When & Then
    assertThatThrownBy(() -> validityValidator.validateMemberWithinReferencedLineValidity(List.of(member)))
        .isInstanceOf(LineV3MemberValidityException.class);
  }

  @Test
  void shouldRejectWhenAnyMemberVersionLiesOutsideParentValidity() {
    // Given
    givenParent(LineVersionV3TestData.lineBuilder().validTo(LocalDate.of(2026, 12, 31)).build());
    LineVersionV3 inside = LineVersionV3TestData.sublineBuilder().validTo(LocalDate.of(2026, 6, 30)).build();
    LineVersionV3 outside = LineVersionV3TestData.sublineBuilder().validFrom(LocalDate.of(2026, 7, 1))
        .validTo(LocalDate.of(2027, 6, 30)).build();

    // When & Then
    assertThatThrownBy(() -> validityValidator.validateMemberWithinReferencedLineValidity(List.of(inside, outside)))
        .isInstanceOf(LineV3MemberValidityException.class);
  }

  @Test
  void shouldRejectMemberAfterVersioningWhenParentDoesNotExist() {
    // Given
    when(lineVersionV3ReadService.findAllBySlnidOrderByValidFrom(PARENT_SLNID)).thenReturn(List.of());
    LineVersionV3 member = LineVersionV3TestData.subline();

    // When & Then
    assertThatThrownBy(() -> validityValidator.validateMemberWithinReferencedLineValidity(List.of(member)))
        .isInstanceOf(SlnidNotFoundException.class);
  }

  @Test
  void shouldAcceptMembersWithinParentOverallValidity() {
    // Given
    when(lineVersionV3ReadService.findAllByReferencedLineSlnid(PARENT_SLNID)).thenReturn(List.of(
        LineVersionV3TestData.sublineBuilder().validFrom(LocalDate.of(2026, 1, 1)).validTo(LocalDate.of(2026, 6, 30)).build(),
        LineVersionV3TestData.sublineBuilder().validFrom(LocalDate.of(2026, 7, 1)).validTo(LocalDate.of(2026, 12, 31))
            .build()));
    List<LineVersionV3> parentVersions = List.of(
        LineVersionV3TestData.lineBuilder().validFrom(LocalDate.of(2026, 1, 1)).validTo(LocalDate.of(2026, 6, 30)).build(),
        LineVersionV3TestData.lineBuilder().validFrom(LocalDate.of(2026, 7, 1)).validTo(LocalDate.of(2026, 12, 31)).build());

    // When & Then
    assertThatCode(() -> validityValidator.validateMembersWithinLineValidity(parentVersions)).doesNotThrowAnyException();
  }

  @Test
  void shouldAcceptParentWithoutMembers() {
    // Given
    when(lineVersionV3ReadService.findAllByReferencedLineSlnid(PARENT_SLNID)).thenReturn(List.of());
    List<LineVersionV3> parentVersions = List.of(LineVersionV3TestData.line());

    // When & Then
    assertThatCode(() -> validityValidator.validateMembersWithinLineValidity(parentVersions)).doesNotThrowAnyException();
  }

  @Test
  void shouldRejectParentWhenMemberEndsAfterParentValidity() {
    // Given
    when(lineVersionV3ReadService.findAllByReferencedLineSlnid(PARENT_SLNID)).thenReturn(List.of(
        LineVersionV3TestData.sublineBuilder().validFrom(LocalDate.of(2026, 1, 1)).validTo(LocalDate.of(2026, 12, 31))
            .build()));
    List<LineVersionV3> parentVersions = List.of(
        LineVersionV3TestData.lineBuilder().validFrom(LocalDate.of(2026, 1, 1)).validTo(LocalDate.of(2026, 6, 30)).build());

    // When & Then
    assertThatThrownBy(() -> validityValidator.validateMembersWithinLineValidity(parentVersions))
        .isInstanceOf(LineV3MembersOutsideValidityException.class)
        .hasFieldOrPropertyWithValue("memberSlnids", List.of(LineVersionV3TestData.CHILD_SLNID));
  }

  @Test
  void shouldRejectParentWhenMemberStartsBeforeParentValidity() {
    // Given
    when(lineVersionV3ReadService.findAllByReferencedLineSlnid(PARENT_SLNID)).thenReturn(List.of(
        LineVersionV3TestData.sublineBuilder().validFrom(LocalDate.of(2026, 1, 1)).validTo(LocalDate.of(2026, 12, 31))
            .build()));
    List<LineVersionV3> parentVersions = List.of(
        LineVersionV3TestData.lineBuilder().validFrom(LocalDate.of(2026, 3, 1)).validTo(LocalDate.of(2026, 12, 31)).build());

    // When & Then
    assertThatThrownBy(() -> validityValidator.validateMembersWithinLineValidity(parentVersions))
        .isInstanceOf(LineV3MembersOutsideValidityException.class)
        .hasFieldOrPropertyWithValue("memberSlnids", List.of(LineVersionV3TestData.CHILD_SLNID));
  }

  @Test
  void shouldListEveryMemberOutsideParentValidityOnce() {
    // Given
    when(lineVersionV3ReadService.findAllByReferencedLineSlnid(PARENT_SLNID)).thenReturn(List.of(
        LineVersionV3TestData.sublineBuilder().validFrom(LocalDate.of(2026, 1, 1)).validTo(LocalDate.of(2026, 6, 30)).build(),
        LineVersionV3TestData.sublineBuilder().validFrom(LocalDate.of(2026, 7, 1)).validTo(LocalDate.of(2026, 12, 31)).build(),
        LineVersionV3TestData.concessionMemberBuilder().validFrom(LocalDate.of(2026, 1, 1)).validTo(LocalDate.of(2026, 12, 31))
            .build(),
        LineVersionV3TestData.sublineBuilder().slnid("ch:1:slnid:1000099").validFrom(LocalDate.of(2026, 1, 1))
            .validTo(LocalDate.of(2026, 3, 31)).build()));
    List<LineVersionV3> parentVersions = List.of(
        LineVersionV3TestData.lineBuilder().validFrom(LocalDate.of(2026, 1, 1)).validTo(LocalDate.of(2026, 6, 30)).build());

    // When & Then
    assertThatThrownBy(() -> validityValidator.validateMembersWithinLineValidity(parentVersions))
        .isInstanceOf(LineV3MembersOutsideValidityException.class)
        .hasFieldOrPropertyWithValue("memberSlnids", List.of(LineVersionV3TestData.CHILD_SLNID, CONCESSION_MEMBER_SLNID));
  }

  private static LineVersionV3 temporaryLine(LocalDate from, LocalDate to) {
    return LineVersionV3TestData.lineBuilder().lineType(LineType.TEMPORARY).concessionType(null).swissLineNumber(null)
        .validFrom(from).validTo(to).build();
  }

}
