package ch.sbb.line.directory.module.line.validation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.exception.SboidNotFoundException;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LineVersionV3ValidationServiceTest {

  @Mock
  private LineV3TypeValidator typeValidator;

  @Mock
  private LineV3ValidityValidator validityValidator;

  @Mock
  private LineV3UpdateValidator updateValidator;

  @Mock
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @InjectMocks
  private LineVersionV3ValidationService validationService;

  @Test
  void shouldApplyLineTypeAndUniquenessAndSboidRulesOnLine() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.line();

    // When
    validationService.validatePreconditionBusinessRules(line);

    // Then
    verify(typeValidator).validateLineType(line);
    verify(typeValidator).validateSwissLineNumberUniqueness(line);
    verify(sharedBusinessOrganisationService).validateSboidExists(line.getBusinessOrganisation());
  }

  @Test
  void shouldApplyMemberTypeAndUniquenessAndSboidRulesOnMember() {
    // Given
    LineVersionV3 member = LineVersionV3TestData.subline();

    // When
    validationService.validatePreconditionBusinessRules(member);

    // Then
    verify(typeValidator).validateMemberType(member);
    verify(typeValidator).validateSwissLineNumberUniqueness(member);
    verify(sharedBusinessOrganisationService).validateSboidExists(member.getBusinessOrganisation());
  }

  @Test
  void shouldRejectVersionWhenSboidDoesNotExist() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.line();
    doThrow(new SboidNotFoundException(line.getBusinessOrganisation()))
        .when(sharedBusinessOrganisationService).validateSboidExists(line.getBusinessOrganisation());

    // When & Then
    assertThatThrownBy(() -> validationService.validatePreconditionBusinessRules(line))
        .isInstanceOf(SboidNotFoundException.class);
  }

  @Test
  void shouldApplyDurationLimitsOnLineAfterVersioning() {
    // Given
    List<LineVersionV3> lineVersions = List.of(LineVersionV3TestData.line());

    // When
    validationService.validateValidityConstraints(lineVersions);

    // Then
    verify(validityValidator).validateLineTypeDurationLimits(lineVersions);
  }

  @Test
  void shouldApplyReferencedLineValidityOnMemberAfterVersioning() {
    // Given
    List<LineVersionV3> memberVersions = List.of(LineVersionV3TestData.subline());

    // When
    validationService.validateValidityConstraints(memberVersions);

    // Then
    verify(validityValidator).validateMemberWithinReferencedLineValidity(memberVersions);
  }

  @Test
  void shouldNotApplyDurationLimitsToMember() {
    // Given
    List<LineVersionV3> memberVersions = List.of(LineVersionV3TestData.subline());

    // When
    validationService.validateValidityConstraints(memberVersions);

    // Then
    verify(validityValidator, never()).validateLineTypeDurationLimits(memberVersions);
  }

  @Test
  void shouldApplyMembersWithinLineValidityOnParent() {
    // Given
    List<LineVersionV3> parentVersions = List.of(LineVersionV3TestData.line());

    // When
    validationService.validateMembersWithinParentValidity(parentVersions);

    // Then
    verify(validityValidator).validateMembersWithinLineValidity(parentVersions);
  }

  @Test
  void shouldApplyRevokedRuleOnUpdate() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.line();

    // When
    validationService.validateNotRevoked(current);

    // Then
    verify(updateValidator).validateNotRevoked(current);
    verifyNoInteractions(typeValidator, validityValidator);
  }

  @Test
  void shouldApplyConcessionFieldRuleOnUpdate() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.line();
    LineVersionV3 edited = current.toBuilder().description("changed").build();

    // When
    validationService.validateUpdate(current, edited);

    // Then
    verify(updateValidator).validateConcessionFieldsUpdatable(current, edited);
    verifyNoInteractions(typeValidator, validityValidator);
  }

}
