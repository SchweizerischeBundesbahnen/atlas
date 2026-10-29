package ch.sbb.line.directory.module.line.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.enumaration.v3.Financing;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.exception.LineV3SupervisorOnlyFieldException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LineV3SupervisorFieldValidatorTest {

  @Mock
  private BusinessOrganisationBasedUserAdministrationService userAdministrationService;

  @InjectMocks
  private LineV3SupervisorFieldValidator supervisorFieldValidator;

  @Test
  void shouldRejectCreationWithSupervisorOnlyFieldsForNonSupervisor() {
    // Given
    givenSupervisor(false);
    LineVersionV3 cableway = LineVersionV3TestData.cableway();

    // When
    Throwable thrown = catchThrowable(() -> supervisorFieldValidator.validateCreate(cableway));

    // Then
    assertThat(thrown).isInstanceOf(LineV3SupervisorOnlyFieldException.class);
    assertThat(((LineV3SupervisorOnlyFieldException) thrown).getFields())
        .containsExactlyInAnyOrder("financing", "cablewayNumber", "cablewayType", "cablewayName");
  }

  @Test
  void shouldAcceptCreationWithSupervisorOnlyFieldsForSupervisor() {
    // Given
    givenSupervisor(true);
    LineVersionV3 cableway = LineVersionV3TestData.cableway();

    // When & Then
    assertThatCode(() -> supervisorFieldValidator.validateCreate(cableway)).doesNotThrowAnyException();
  }

  @Test
  void shouldAcceptCreationWithoutSupervisorOnlyFieldsWithoutCheckingTheRole() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().financing(null).build();

    // When & Then
    assertThatCode(() -> supervisorFieldValidator.validateCreate(line)).doesNotThrowAnyException();
  }

  @Test
  void shouldAcceptUpdateResendingTheUnchangedSupervisorOnlyFieldsForNonSupervisor() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.cableway();
    LineVersionV3 edited = current.toBuilder().description("changed").build();

    // When & Then
    assertThatCode(() -> supervisorFieldValidator.validateUpdate(current, edited)).doesNotThrowAnyException();
  }

  @Test
  void shouldRejectUpdateChangingASupervisorOnlyFieldForNonSupervisor() {
    // Given
    givenSupervisor(false);
    LineVersionV3 current = LineVersionV3TestData.line();
    LineVersionV3 edited = current.toBuilder().financing(Financing.RPV).build();

    // When
    Throwable thrown = catchThrowable(() -> supervisorFieldValidator.validateUpdate(current, edited));

    // Then
    assertThat(thrown).isInstanceOf(LineV3SupervisorOnlyFieldException.class);
    assertThat(((LineV3SupervisorOnlyFieldException) thrown).getFields()).containsExactly("financing");
  }

  @Test
  void shouldRejectUpdateRemovingASupervisorOnlyFieldForNonSupervisor() {
    // Given
    givenSupervisor(false);
    LineVersionV3 current = LineVersionV3TestData.cableway();
    LineVersionV3 edited = current.toBuilder().cablewayName(null).build();

    // When
    Throwable thrown = catchThrowable(() -> supervisorFieldValidator.validateUpdate(current, edited));

    // Then
    assertThat(thrown).isInstanceOf(LineV3SupervisorOnlyFieldException.class);
    assertThat(((LineV3SupervisorOnlyFieldException) thrown).getFields()).containsExactly("cablewayName");
  }

  @Test
  void shouldAcceptUpdateChangingASupervisorOnlyFieldForSupervisor() {
    // Given
    givenSupervisor(true);
    LineVersionV3 current = LineVersionV3TestData.line();
    LineVersionV3 edited = current.toBuilder().financing(Financing.RPV).build();

    // When & Then
    assertThatCode(() -> supervisorFieldValidator.validateUpdate(current, edited)).doesNotThrowAnyException();
  }

  private void givenSupervisor(boolean supervisor) {
    when(userAdministrationService.isAtLeastSupervisor(ApplicationType.LIDI)).thenReturn(supervisor);
  }

}
