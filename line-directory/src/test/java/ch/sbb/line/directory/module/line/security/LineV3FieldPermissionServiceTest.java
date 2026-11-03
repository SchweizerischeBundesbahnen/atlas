package ch.sbb.line.directory.module.line.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.enumaration.v3.Financing;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LineV3FieldPermissionServiceTest {

  @Mock
  private BusinessOrganisationBasedUserAdministrationService userAdministrationService;

  @InjectMocks
  private LineV3FieldPermissionService fieldPermissionService;

  @Test
  void shouldResolveAllRestrictedFieldsFromTheAnnotationOnCreate() {
    // Given
    givenSupervisor(false);
    LineVersionV3 cableway = LineVersionV3TestData.cableway();

    // When
    Set<String> forbiddenFields = fieldPermissionService.forbiddenFieldsOnCreate(cableway);

    // Then
    assertThat(forbiddenFields).containsExactlyInAnyOrder("financing", "cablewayNumber", "cablewayType", "cablewayName");
  }

  @Test
  void shouldAllowRestrictedFieldsOnCreateForSupervisor() {
    // Given
    givenSupervisor(true);
    LineVersionV3 cableway = LineVersionV3TestData.cableway();

    // When & Then
    assertThat(fieldPermissionService.forbiddenFieldsOnCreate(cableway)).isEmpty();
  }

  @Test
  void shouldNotEvaluateThePermissionWhenNoRestrictedFieldIsPopulatedOnCreate() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().financing(null).build();

    // When & Then
    assertThat(fieldPermissionService.forbiddenFieldsOnCreate(line)).isEmpty();
  }

  @Test
  void shouldNotEvaluateThePermissionWhenResendingTheUnchangedRestrictedFieldsOnUpdate() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.cableway();
    LineVersionV3 edited = current.toBuilder().description("changed").build();

    // When & Then
    assertThat(fieldPermissionService.forbiddenFieldsOnUpdate(current, edited)).isEmpty();
  }

  @Test
  void shouldRejectChangingARestrictedFieldOnUpdate() {
    // Given
    givenSupervisor(false);
    LineVersionV3 current = LineVersionV3TestData.line();
    LineVersionV3 edited = current.toBuilder().financing(Financing.RPV).build();

    // When & Then
    assertThat(fieldPermissionService.forbiddenFieldsOnUpdate(current, edited)).containsExactly("financing");
  }

  @Test
  void shouldRejectRemovingARestrictedFieldOnUpdate() {
    // Given
    givenSupervisor(false);
    LineVersionV3 current = LineVersionV3TestData.cableway();
    LineVersionV3 edited = current.toBuilder().cablewayName(null).build();

    // When & Then
    assertThat(fieldPermissionService.forbiddenFieldsOnUpdate(current, edited)).containsExactly("cablewayName");
  }

  @Test
  void shouldAllowChangingARestrictedFieldOnUpdateForSupervisor() {
    // Given
    givenSupervisor(true);
    LineVersionV3 current = LineVersionV3TestData.line();
    LineVersionV3 edited = current.toBuilder().financing(Financing.RPV).build();

    // When & Then
    assertThat(fieldPermissionService.forbiddenFieldsOnUpdate(current, edited)).isEmpty();
  }

  @Test
  void shouldGrantWritingRestrictedFieldsOnlyWhenNothingElseChanged() {
    // Given
    givenSupervisor(true);
    LineVersionV3 current = LineVersionV3TestData.line();
    LineVersionV3 edited = current.toBuilder().financing(Financing.RPV).build();

    // When & Then
    assertThat(fieldPermissionService.mayWriteRestrictedFieldsOnly(edited, List.of(current))).isTrue();
  }

  @Test
  void shouldDenyWritingRestrictedFieldsOnlyWithoutThePermission() {
    // Given
    givenSupervisor(false);
    LineVersionV3 current = LineVersionV3TestData.line();
    LineVersionV3 edited = current.toBuilder().financing(Financing.RPV).build();

    // When & Then
    assertThat(fieldPermissionService.mayWriteRestrictedFieldsOnly(edited, List.of(current))).isFalse();
  }

  @Test
  void shouldDenyWritingRestrictedFieldsOnlyWhenAnUnrestrictedFieldChangedAsWell() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.line();
    LineVersionV3 edited = current.toBuilder().financing(Financing.RPV).description("changed").build();

    // When & Then
    assertThat(fieldPermissionService.mayWriteRestrictedFieldsOnly(edited, List.of(current))).isFalse();
  }

  @Test
  void shouldDenyWritingRestrictedFieldsOnlyWhenNothingChanged() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.line();

    // When & Then
    assertThat(fieldPermissionService.mayWriteRestrictedFieldsOnly(current.toBuilder().build(), List.of(current))).isFalse();
  }

  @Test
  void shouldDenyWritingRestrictedFieldsOnlyWhenAnotherAffectedVersionIsOverwritten() {
    // Given
    LineVersionV3 firstVersion = LineVersionV3TestData.lineBuilder()
        .validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(2026, 6, 30))
        .build();
    LineVersionV3 secondVersion = firstVersion.toBuilder()
        .description("other description")
        .validFrom(LocalDate.of(2026, 7, 1))
        .validTo(LocalDate.of(2026, 12, 31))
        .build();
    LineVersionV3 edited = firstVersion.toBuilder()
        .financing(Financing.RPV)
        .validTo(LocalDate.of(2026, 12, 31))
        .build();

    // When & Then
    assertThat(fieldPermissionService.mayWriteRestrictedFieldsOnly(edited, List.of(firstVersion, secondVersion))).isFalse();
  }

  @Test
  void shouldDenyWritingRestrictedFieldsOnlyWhenNoCurrentVersionIsAffected() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.lineBuilder()
        .validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(2026, 6, 30))
        .build();
    LineVersionV3 edited = current.toBuilder()
        .financing(Financing.RPV)
        .validFrom(LocalDate.of(2027, 1, 1))
        .validTo(LocalDate.of(2027, 12, 31))
        .build();

    // When & Then
    assertThat(fieldPermissionService.mayWriteRestrictedFieldsOnly(edited, List.of(current))).isFalse();
  }

  private void givenSupervisor(boolean supervisor) {
    when(userAdministrationService.isAtLeastSupervisor(ApplicationType.LIDI)).thenReturn(supervisor);
  }

}
