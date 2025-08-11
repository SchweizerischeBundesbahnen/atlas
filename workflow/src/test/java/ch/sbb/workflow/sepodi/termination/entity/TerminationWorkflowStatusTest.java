package ch.sbb.workflow.sepodi.termination.entity;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TerminationWorkflowStatusTest {

  @ParameterizedTest
  @EnumSource(value = TerminationWorkflowStatus.class, names = {"TARIFF_STOP_NOT_APPROVED", "TARIFF_STOP_APPROVED", "CANCELED"})
  void shouldValidateWorkflowStatusTransitionFromStartedToAllowedStatus(TerminationWorkflowStatus status) {
    //when && then
    assertDoesNotThrow(
        () -> TerminationWorkflowStatus.validateWorkflowStatusTransition(TerminationWorkflowStatus.STARTED, status));
  }

  @ParameterizedTest
  @EnumSource(value = TerminationWorkflowStatus.class, names = {"TERMINATION_APPROVED", "TERMINATION_NOT_APPROVED", "CANCELED"})
  void shouldValidateWorkflowStatusTransitionFromTariffStopPointApprovedToAllowedStatus(TerminationWorkflowStatus status) {
    //when && then
    assertDoesNotThrow(
        () -> TerminationWorkflowStatus.validateWorkflowStatusTransition(TerminationWorkflowStatus.TARIFF_STOP_APPROVED, status));
  }

  @ParameterizedTest
  @EnumSource(value = TerminationWorkflowStatus.class, names = {"TERMINATION_APPROVED", "TERMINATION_NOT_APPROVED_CLOSED"})
  void shouldValidateWorkflowStatusTransitionFromTerminationNotApprovedToAllowedStatus(TerminationWorkflowStatus status) {
    //when && then
    assertDoesNotThrow(
        () -> TerminationWorkflowStatus.validateWorkflowStatusTransition(TerminationWorkflowStatus.TERMINATION_NOT_APPROVED,
            status));
  }

  @ParameterizedTest
  @EnumSource(value = TerminationWorkflowStatus.class, names = {"STARTED", "TARIFF_STOP_APPROVED", "TERMINATION_NOT_APPROVED"})
  void shouldValidateTerminationIsAbortible(TerminationWorkflowStatus status) {
    //when && then
    assertDoesNotThrow(
        () -> TerminationWorkflowStatus.validateTerminationIsAbortible(status));
  }

}