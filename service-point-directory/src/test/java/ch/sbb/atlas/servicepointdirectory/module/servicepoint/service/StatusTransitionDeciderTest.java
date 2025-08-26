package ch.sbb.atlas.servicepointdirectory.module.servicepoint.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.ServicePointStatusChangeNotAllowedException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class StatusTransitionDeciderTest {

  @ParameterizedTest
  @EnumSource(value = Status.class, names = {"IN_REVIEW", "VALIDATED"})
  void shouldValidateStatusTransitionFromDraftToAllowedStatus(Status status) {
    //when && then
    assertDoesNotThrow(() -> StatusTransitionDecider.validateWorkflowStatusTransition(Status.DRAFT, status));
  }

  @ParameterizedTest
  @EnumSource(value = Status.class, names = {"DRAFT", "REVOKED", "WITHDRAWN"})
  void shouldNotValidateStatusTransitionFromDraftToNotAllowedStatus(Status status) {
    //when && then
    assertThrows(ServicePointStatusChangeNotAllowedException.class,
        () -> StatusTransitionDecider.validateWorkflowStatusTransition(Status.DRAFT, status));
  }

  @ParameterizedTest
  @EnumSource(value = Status.class, names = {"DRAFT", "VALIDATED"})
  void shouldValidateStatusTransitionFromInReviewToAllowedStatus(Status status) {
    //when && then
    assertDoesNotThrow(() -> StatusTransitionDecider.validateWorkflowStatusTransition(Status.IN_REVIEW, status));
  }

  @ParameterizedTest
  @EnumSource(value = Status.class, names = {"IN_REVIEW", "REVOKED", "WITHDRAWN"})
  void shouldNotValidateStatusTransitionFromInReviewToNotAllowedStatus(Status status) {
    //when && then
    assertThrows(ServicePointStatusChangeNotAllowedException.class,
        () -> StatusTransitionDecider.validateWorkflowStatusTransition(Status.IN_REVIEW, status));
  }

}
