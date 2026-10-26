package ch.sbb.line.directory.module.line.validation;

import static ch.sbb.line.directory.module.line.LineVersionV3TestData.CONCESSION_MEMBER_SLNID;
import static ch.sbb.line.directory.module.line.LineVersionV3TestData.PARENT_SLNID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.enumaration.v3.ConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.SboidNotFoundException;
import ch.sbb.line.directory.exception.RevokedException;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.exception.LineV3ConflictException;
import ch.sbb.line.directory.module.line.exception.LineV3FieldNotUpdatableException;
import ch.sbb.line.directory.module.line.exception.LineV3MemberConcessionException;
import ch.sbb.line.directory.module.line.exception.LineV3MemberTypeMismatchException;
import ch.sbb.line.directory.module.line.exception.LineV3MemberValidityException;
import ch.sbb.line.directory.module.line.exception.LineV3MembersOutsideValidityException;
import ch.sbb.line.directory.module.line.exception.LineV3TypeNotAllowedException;
import ch.sbb.line.directory.module.line.exception.LineV3TypeOrderlyException;
import ch.sbb.line.directory.module.line.exception.OrderlyLineValidityException;
import ch.sbb.line.directory.module.line.exception.ReferencedLineHasParentException;
import ch.sbb.line.directory.module.line.exception.ReferencedLineRevokedException;
import ch.sbb.line.directory.module.line.exception.TemporaryLineValidationException;
import ch.sbb.line.directory.module.line.repository.LineVersionV3Repository;
import java.time.LocalDate;
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
class LineVersionV3ValidationServiceTest {

  @Mock
  private LineVersionV3Repository lineVersionV3Repository;

  @Mock
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  private LineVersionV3ValidationService validationService;

  @BeforeEach
  void setUp() {
    validationService = new LineVersionV3ValidationService(lineVersionV3Repository, sharedBusinessOrganisationService);
  }

  private void givenParent(LineVersionV3... parentVersions) {
    when(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(PARENT_SLNID)).thenReturn(List.of(parentVersions));
  }

  // ---------- LINE: precondition ----------

  @Test
  void shouldAcceptValidOrderlyLine() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.line();

    // When & Then
    assertThatCode(() -> validationService.validatePreconditionBusinessRules(line)).doesNotThrowAnyException();
    verify(sharedBusinessOrganisationService).validateSboidExists(line.getBusinessOrganisation());
  }

  @Test
  void shouldRejectLineWhenSboidDoesNotExist() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.line();
    doThrow(new SboidNotFoundException(line.getBusinessOrganisation()))
        .when(sharedBusinessOrganisationService).validateSboidExists(line.getBusinessOrganisation());

    // When & Then
    assertThatThrownBy(() -> validationService.validatePreconditionBusinessRules(line))
        .isInstanceOf(SboidNotFoundException.class);
  }

  @ParameterizedTest
  @EnumSource(value = LineType.class, names = {"TECHNICAL", "CONCESSION"})
  void shouldRejectLineWithMemberOnlyLineType(LineType lineType) {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().lineType(lineType).concessionType(null).swissLineNumber(null)
        .build();

    // When & Then
    assertThatThrownBy(() -> validationService.validatePreconditionBusinessRules(line))
        .isInstanceOf(LineV3TypeNotAllowedException.class);
  }

  @ParameterizedTest
  @EnumSource(value = LineType.class, names = {"DISPOSITION", "TEMPORARY", "OPERATIONAL"})
  void shouldAcceptNonOrderlyLineWithoutConcessionTypeAndSwissLineNumber(LineType lineType) {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().lineType(lineType).concessionType(null).swissLineNumber(null)
        .build();

    // When & Then
    assertThatCode(() -> validationService.validatePreconditionBusinessRules(line)).doesNotThrowAnyException();
  }

  @Test
  void shouldRejectOrderlyLineWithoutConcessionType() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().concessionType(null).build();

    // When & Then
    assertThatThrownBy(() -> validationService.validatePreconditionBusinessRules(line))
        .isInstanceOf(LineV3TypeOrderlyException.class);
  }

  @Test
  void shouldRejectOrderlyLineWithoutSwissLineNumber() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().swissLineNumber(null).build();

    // When & Then
    assertThatThrownBy(() -> validationService.validatePreconditionBusinessRules(line))
        .isInstanceOf(LineV3TypeOrderlyException.class);
  }

  @ParameterizedTest
  @EnumSource(value = LineType.class, names = {"DISPOSITION", "TEMPORARY", "OPERATIONAL"})
  void shouldRejectNonOrderlyLineWithSwissLineNumber(LineType lineType) {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().lineType(lineType).concessionType(null).build();

    // When & Then
    assertThatThrownBy(() -> validationService.validatePreconditionBusinessRules(line))
        .isInstanceOf(LineV3TypeOrderlyException.class);
  }

  @ParameterizedTest
  @EnumSource(value = LineType.class, names = {"DISPOSITION", "TEMPORARY", "OPERATIONAL"})
  void shouldRejectNonOrderlyLineWithConcessionType(LineType lineType) {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().lineType(lineType).swissLineNumber(null).build();

    // When & Then
    assertThatThrownBy(() -> validationService.validatePreconditionBusinessRules(line))
        .isInstanceOf(LineV3TypeOrderlyException.class);
  }

  @Test
  void shouldRejectOrderlyLineWhenSwissLineNumberOverlapsWithOtherSlnid() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.line();
    LineVersionV3 other = LineVersionV3TestData.lineBuilder().slnid("ch:1:slnid:1999999").build();
    when(lineVersionV3Repository.findSwissLineNumberOverlaps(line)).thenReturn(List.of(other));

    // When & Then
    assertThatThrownBy(() -> validationService.validatePreconditionBusinessRules(line))
        .isInstanceOf(LineV3ConflictException.class);
  }

  @Test
  void shouldNotCheckSwissLineNumberOverlapForNonOrderlyLine() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().lineType(LineType.TEMPORARY).concessionType(null)
        .swissLineNumber(null).build();
    when(lineVersionV3Repository.findSwissLineNumberOverlaps(any())).thenReturn(List.of(line));

    // When & Then
    assertThatCode(() -> validationService.validatePreconditionBusinessRules(line)).doesNotThrowAnyException();
  }

  // ---------- SUBLINE / member: precondition ----------

  @Test
  void shouldAcceptValidTechnicalMember() {
    // Given
    givenParent(LineVersionV3TestData.line());
    LineVersionV3 member = LineVersionV3TestData.subline();

    // When & Then
    assertThatCode(() -> validationService.validatePreconditionBusinessRules(member)).doesNotThrowAnyException();
    verify(sharedBusinessOrganisationService).validateSboidExists(member.getBusinessOrganisation());
  }

  @Test
  void shouldRejectMemberWhenSboidDoesNotExist() {
    // Given
    givenParent(LineVersionV3TestData.line());
    LineVersionV3 member = LineVersionV3TestData.subline();
    doThrow(new SboidNotFoundException(member.getBusinessOrganisation()))
        .when(sharedBusinessOrganisationService).validateSboidExists(member.getBusinessOrganisation());

    // When & Then
    assertThatThrownBy(() -> validationService.validatePreconditionBusinessRules(member))
        .isInstanceOf(SboidNotFoundException.class);
  }

  @Test
  void shouldRejectMemberWhenParentDoesNotExist() {
    // Given
    when(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(PARENT_SLNID)).thenReturn(List.of());
    LineVersionV3 member = LineVersionV3TestData.subline();

    // When & Then
    assertThatThrownBy(() -> validationService.validatePreconditionBusinessRules(member))
        .isInstanceOf(SlnidNotFoundException.class);
  }

  @Test
  void shouldRejectMemberWhenParentIsRevoked() {
    // Given
    givenParent(LineVersionV3TestData.lineBuilder().status(Status.REVOKED).build());
    LineVersionV3 member = LineVersionV3TestData.subline();

    // When & Then
    assertThatThrownBy(() -> validationService.validatePreconditionBusinessRules(member))
        .isInstanceOf(ReferencedLineRevokedException.class);
  }

  @Test
  void shouldRejectMemberWhenParentIsItselfAMember() {
    // Given
    givenParent(LineVersionV3TestData.lineBuilder().referencedLineSlnid("ch:1:slnid:1000001").build());
    LineVersionV3 member = LineVersionV3TestData.subline();

    // When & Then
    assertThatThrownBy(() -> validationService.validatePreconditionBusinessRules(member))
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
    assertThatCode(() -> validationService.validatePreconditionBusinessRules(member)).doesNotThrowAnyException();
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
    assertThatThrownBy(() -> validationService.validatePreconditionBusinessRules(member))
        .isInstanceOf(LineV3MemberTypeMismatchException.class);
  }

  @Test
  void shouldRejectConcessionMemberWithoutConcessionType() {
    // Given
    givenParent(LineVersionV3TestData.line());
    LineVersionV3 member = LineVersionV3TestData.concessionMemberBuilder().concessionType(null).build();

    // When & Then
    assertThatThrownBy(() -> validationService.validatePreconditionBusinessRules(member))
        .isInstanceOf(LineV3MemberConcessionException.class);
  }

  @Test
  void shouldRejectConcessionMemberWithoutSwissLineNumber() {
    // Given
    givenParent(LineVersionV3TestData.line());
    LineVersionV3 member = LineVersionV3TestData.concessionMemberBuilder().swissLineNumber(null).build();

    // When & Then
    assertThatThrownBy(() -> validationService.validatePreconditionBusinessRules(member))
        .isInstanceOf(LineV3MemberConcessionException.class);
  }

  @Test
  void shouldRejectTechnicalMemberWithConcessionType() {
    // Given
    givenParent(LineVersionV3TestData.line());
    LineVersionV3 member = LineVersionV3TestData.sublineBuilder().concessionType(ConcessionType.CANTONALLY_APPROVED_LINE)
        .build();

    // When & Then
    assertThatThrownBy(() -> validationService.validatePreconditionBusinessRules(member))
        .isInstanceOf(LineV3MemberConcessionException.class);
  }

  @Test
  void shouldRejectTechnicalMemberWithSwissLineNumber() {
    // Given
    givenParent(LineVersionV3TestData.line());
    LineVersionV3 member = LineVersionV3TestData.sublineBuilder().swissLineNumber("90.583.2").build();

    // When & Then
    assertThatThrownBy(() -> validationService.validatePreconditionBusinessRules(member))
        .isInstanceOf(LineV3MemberConcessionException.class);
  }

  @Test
  void shouldAcceptConcessionMemberWithConcessionTypeAndSwissLineNumber() {
    // Given
    givenParent(LineVersionV3TestData.line());
    LineVersionV3 member = LineVersionV3TestData.concessionMemberBuilder().build();

    // When & Then
    assertThatCode(() -> validationService.validatePreconditionBusinessRules(member)).doesNotThrowAnyException();
  }

  @Test
  void shouldRejectConcessionMemberWhenSwissLineNumberOverlapsWithOtherSlnid() {
    // Given
    givenParent(LineVersionV3TestData.line());
    LineVersionV3 member = LineVersionV3TestData.concessionMemberBuilder().build();
    LineVersionV3 other = LineVersionV3TestData.lineBuilder().slnid("ch:1:slnid:1999999").swissLineNumber("90.583.1").build();
    when(lineVersionV3Repository.findSwissLineNumberOverlaps(member)).thenReturn(List.of(other));

    // When & Then
    assertThatThrownBy(() -> validationService.validatePreconditionBusinessRules(member))
        .isInstanceOf(LineV3ConflictException.class);
  }

  @Test
  void shouldProduceConflictErrorResponseWithSwissLineNumberDetail() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.line();
    LineVersionV3 other = LineVersionV3TestData.lineBuilder().slnid("ch:1:slnid:1999999").build();

    // When
    var response = new LineV3ConflictException(line, List.of(other)).getErrorResponse();

    // Then
    assertThat(response.getStatus()).isEqualTo(409);
    assertThat(response.getDetails()).hasSize(1);
    assertThat(response.getDetails().first().getDisplayInfo().getCode()).isEqualTo("LIDI.LINE.CONFLICT.SWISS_NUMBER");
  }

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

  @Test
  void shouldReturnUnprocessableContentForDurationRules() {
    // Given
    LocalDate from = LocalDate.of(2026, 1, 1);
    LocalDate to = LocalDate.of(2026, 1, 31);

    // When & Then
    assertThat(new TemporaryLineValidationException(from, to).getErrorResponse().getStatus()).isEqualTo(422);
    assertThat(new OrderlyLineValidityException(from, to).getErrorResponse().getStatus()).isEqualTo(422);
  }

  // ---------- after versioning ----------

  @Test
  void shouldRejectTemporaryLineWithTotalValidityOver14Days() {
    // Given
    LineVersionV3 first = temporaryLine(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10));
    LineVersionV3 second = temporaryLine(LocalDate.of(2026, 1, 11), LocalDate.of(2026, 1, 15));

    // When & Then
    assertThatThrownBy(() -> validationService.validateValidityConstraints(List.of(first, second)))
        .isInstanceOf(TemporaryLineValidationException.class);
  }

  @Test
  void shouldAcceptTemporaryLineWithTotalValidityOf14DaysAcrossVersions() {
    // Given
    LineVersionV3 first = temporaryLine(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10));
    LineVersionV3 second = temporaryLine(LocalDate.of(2026, 1, 11), LocalDate.of(2026, 1, 14));

    // When & Then
    assertThatCode(() -> validationService.validateValidityConstraints(List.of(first, second)))
        .doesNotThrowAnyException();
  }

  @Test
  void shouldRejectOrderlyLineWithTotalValidityUnder15Days() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(2026, 1, 14)).build();

    // When & Then
    assertThatThrownBy(() -> validationService.validateValidityConstraints(List.of(line)))
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
    assertThatCode(() -> validationService.validateValidityConstraints(List.of(first, second)))
        .doesNotThrowAnyException();
  }

  @Test
  void shouldNotApplyDurationRulesToTemporaryMember() {
    // Given
    givenParent(parentOfType(LineType.TEMPORARY));
    LineVersionV3 member = memberOfType(LineType.TEMPORARY);

    // When & Then
    assertThatCode(() -> validationService.validateValidityConstraints(List.of(member)))
        .doesNotThrowAnyException();
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
    assertThatCode(() -> validationService.validateValidityConstraints(List.of(member)))
        .doesNotThrowAnyException();
  }

  @Test
  void shouldRejectMemberStartingBeforeParentValidity() {
    // Given
    givenParent(LineVersionV3TestData.line());
    LineVersionV3 member = LineVersionV3TestData.sublineBuilder().validFrom(LocalDate.of(2025, 12, 31)).build();

    // When & Then
    assertThatThrownBy(() -> validationService.validateValidityConstraints(List.of(member)))
        .isInstanceOf(LineV3MemberValidityException.class);
  }

  @Test
  void shouldRejectMemberEndingAfterParentValidity() {
    // Given
    givenParent(LineVersionV3TestData.lineBuilder().validTo(LocalDate.of(2026, 12, 31)).build());
    LineVersionV3 member = LineVersionV3TestData.sublineBuilder().validTo(LocalDate.of(2027, 1, 1)).build();

    // When & Then
    assertThatThrownBy(() -> validationService.validateValidityConstraints(List.of(member)))
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
    assertThatThrownBy(() -> validationService.validateValidityConstraints(List.of(inside, outside)))
        .isInstanceOf(LineV3MemberValidityException.class);
  }

  @Test
  void shouldRejectMemberAfterVersioningWhenParentDoesNotExist() {
    // Given
    when(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(PARENT_SLNID)).thenReturn(List.of());
    LineVersionV3 member = LineVersionV3TestData.subline();

    // When & Then
    assertThatThrownBy(() -> validationService.validateValidityConstraints(List.of(member)))
        .isInstanceOf(SlnidNotFoundException.class);
  }

  // ---------- parent validity vs. members ----------

  @Test
  void shouldAcceptMembersWithinParentOverallValidity() {
    // Given
    when(lineVersionV3Repository.findAllByReferencedLineSlnid(PARENT_SLNID)).thenReturn(List.of(
        LineVersionV3TestData.sublineBuilder().validFrom(LocalDate.of(2026, 1, 1)).validTo(LocalDate.of(2026, 6, 30)).build(),
        LineVersionV3TestData.sublineBuilder().validFrom(LocalDate.of(2026, 7, 1)).validTo(LocalDate.of(2026, 12, 31)).build()));
    List<LineVersionV3> parentVersions = List.of(
        LineVersionV3TestData.lineBuilder().validFrom(LocalDate.of(2026, 1, 1)).validTo(LocalDate.of(2026, 6, 30)).build(),
        LineVersionV3TestData.lineBuilder().validFrom(LocalDate.of(2026, 7, 1)).validTo(LocalDate.of(2026, 12, 31)).build());

    // When & Then
    assertThatCode(() -> validationService.validateMembersWithinParentValidity(parentVersions)).doesNotThrowAnyException();
  }

  @Test
  void shouldAcceptParentWithoutMembers() {
    // Given
    when(lineVersionV3Repository.findAllByReferencedLineSlnid(PARENT_SLNID)).thenReturn(List.of());
    List<LineVersionV3> parentVersions = List.of(LineVersionV3TestData.line());

    // When & Then
    assertThatCode(() -> validationService.validateMembersWithinParentValidity(parentVersions)).doesNotThrowAnyException();
  }

  @Test
  void shouldRejectParentWhenMemberEndsAfterParentValidity() {
    // Given
    when(lineVersionV3Repository.findAllByReferencedLineSlnid(PARENT_SLNID)).thenReturn(List.of(
        LineVersionV3TestData.sublineBuilder().validFrom(LocalDate.of(2026, 1, 1)).validTo(LocalDate.of(2026, 12, 31)).build()));
    List<LineVersionV3> parentVersions = List.of(
        LineVersionV3TestData.lineBuilder().validFrom(LocalDate.of(2026, 1, 1)).validTo(LocalDate.of(2026, 6, 30)).build());

    // When & Then
    assertThatThrownBy(() -> validationService.validateMembersWithinParentValidity(parentVersions))
        .isInstanceOf(LineV3MembersOutsideValidityException.class)
        .hasFieldOrPropertyWithValue("memberSlnids", List.of(LineVersionV3TestData.CHILD_SLNID));
  }

  @Test
  void shouldRejectParentWhenMemberStartsBeforeParentValidity() {
    // Given
    when(lineVersionV3Repository.findAllByReferencedLineSlnid(PARENT_SLNID)).thenReturn(List.of(
        LineVersionV3TestData.sublineBuilder().validFrom(LocalDate.of(2026, 1, 1)).validTo(LocalDate.of(2026, 12, 31)).build()));
    List<LineVersionV3> parentVersions = List.of(
        LineVersionV3TestData.lineBuilder().validFrom(LocalDate.of(2026, 3, 1)).validTo(LocalDate.of(2026, 12, 31)).build());

    // When & Then
    assertThatThrownBy(() -> validationService.validateMembersWithinParentValidity(parentVersions))
        .isInstanceOf(LineV3MembersOutsideValidityException.class)
        .hasFieldOrPropertyWithValue("memberSlnids", List.of(LineVersionV3TestData.CHILD_SLNID));
  }

  @Test
  void shouldListEveryMemberOutsideParentValidityOnce() {
    // Given
    when(lineVersionV3Repository.findAllByReferencedLineSlnid(PARENT_SLNID)).thenReturn(List.of(
        LineVersionV3TestData.sublineBuilder().validFrom(LocalDate.of(2026, 1, 1)).validTo(LocalDate.of(2026, 6, 30)).build(),
        LineVersionV3TestData.sublineBuilder().validFrom(LocalDate.of(2026, 7, 1)).validTo(LocalDate.of(2026, 12, 31)).build(),
        LineVersionV3TestData.concessionMemberBuilder().validFrom(LocalDate.of(2026, 1, 1)).validTo(LocalDate.of(2026, 12, 31))
            .build(),
        LineVersionV3TestData.sublineBuilder().slnid("ch:1:slnid:1000099").validFrom(LocalDate.of(2026, 1, 1))
            .validTo(LocalDate.of(2026, 3, 31)).build()));
    List<LineVersionV3> parentVersions = List.of(
        LineVersionV3TestData.lineBuilder().validFrom(LocalDate.of(2026, 1, 1)).validTo(LocalDate.of(2026, 6, 30)).build());

    // When & Then
    assertThatThrownBy(() -> validationService.validateMembersWithinParentValidity(parentVersions))
        .isInstanceOf(LineV3MembersOutsideValidityException.class)
        .hasFieldOrPropertyWithValue("memberSlnids", List.of(LineVersionV3TestData.CHILD_SLNID, CONCESSION_MEMBER_SLNID));
  }

  // ---------- update ----------

  @Test
  void shouldRejectUpdateOfRevokedLine() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.lineBuilder().status(Status.REVOKED).build();

    // When & Then
    assertThatThrownBy(() -> validationService.validateNotRevoked(current)).isInstanceOf(RevokedException.class);
  }

  @Test
  void shouldRejectUpdateOfRevokedMember() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.sublineBuilder().status(Status.REVOKED).build();

    // When & Then
    assertThatThrownBy(() -> validationService.validateNotRevoked(current)).isInstanceOf(RevokedException.class);
  }

  @Test
  void shouldAcceptUpdateOfValidatedLine() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.line();

    // When & Then
    assertThatCode(() -> validationService.validateNotRevoked(current)).doesNotThrowAnyException();
  }

  @Test
  void shouldRejectUpdatingSwissLineNumberOfNonOrderlyLine() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.lineBuilder().lineType(LineType.TEMPORARY).concessionType(null)
        .swissLineNumber(null).build();
    LineVersionV3 edited = current.toBuilder().swissLineNumber("90.1").build();

    // When & Then
    assertThatThrownBy(() -> validationService.validateUpdate(current, edited))
        .isInstanceOf(LineV3FieldNotUpdatableException.class);
  }

  @Test
  void shouldRejectUpdatingConcessionTypeOfNonOrderlyLine() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.lineBuilder().lineType(LineType.DISPOSITION).concessionType(null)
        .swissLineNumber(null).build();
    LineVersionV3 edited = current.toBuilder().concessionType(ConcessionType.CANTONALLY_APPROVED_LINE).build();

    // When & Then
    assertThatThrownBy(() -> validationService.validateUpdate(current, edited))
        .isInstanceOf(LineV3FieldNotUpdatableException.class);
  }

  @Test
  void shouldAcceptUpdateOfNonOrderlyLineWithoutSwissLineNumberAndConcessionType() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.lineBuilder().lineType(LineType.OPERATIONAL).concessionType(null)
        .swissLineNumber(null).build();
    LineVersionV3 edited = current.toBuilder().description("changed").build();

    // When & Then
    assertThatCode(() -> validationService.validateUpdate(current, edited)).doesNotThrowAnyException();
  }

  @Test
  void shouldAcceptUpdateOfSwissLineNumberOfOrderlyLine() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.line();
    LineVersionV3 edited = current.toBuilder().swissLineNumber("90.999").build();

    // When & Then
    assertThatCode(() -> validationService.validateUpdate(current, edited)).doesNotThrowAnyException();
  }

  @Test
  void shouldAcceptUpdateOfConcessionMemberSwissLineNumber() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.concessionMemberBuilder().build();
    LineVersionV3 edited = current.toBuilder().swissLineNumber("90.583.9").build();

    // When & Then
    assertThat(current.getSlnid()).isEqualTo(CONCESSION_MEMBER_SLNID);
    assertThatCode(() -> validationService.validateUpdate(current, edited)).doesNotThrowAnyException();
  }

  // ---------- helpers ----------

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

  private static LineVersionV3 temporaryLine(LocalDate from, LocalDate to) {
    return LineVersionV3TestData.lineBuilder().lineType(LineType.TEMPORARY).concessionType(null).swissLineNumber(null)
        .validFrom(from).validTo(to).build();
  }

}
