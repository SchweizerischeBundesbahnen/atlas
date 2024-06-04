package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointStatusChangeNotAllowedException;
import org.junit.jupiter.api.Test;

public class StatusTransitionDeciderTest {

  @Test
  void shouldValidateStatusTransitionFromDraftToInReview(){
    //when && then
    assertDoesNotThrow(()-> StatusTransitionDecider.validateStatusTransition(Status.DRAFT,Status.IN_REVIEW));
  }

  @Test
  void shouldValidateStatusTransitionFromDraftToValidated(){
    //when && then
    assertDoesNotThrow(()-> StatusTransitionDecider.validateStatusTransition(Status.DRAFT,Status.VALIDATED));
  }

  @Test
  void shouldNotValidateStatusTransitionFromReviewToInReview(){
    //when && then
    assertThrows(ServicePointStatusChangeNotAllowedException.class,
        ()-> StatusTransitionDecider.validateStatusTransition(Status.IN_REVIEW,Status.IN_REVIEW));
  }

}
