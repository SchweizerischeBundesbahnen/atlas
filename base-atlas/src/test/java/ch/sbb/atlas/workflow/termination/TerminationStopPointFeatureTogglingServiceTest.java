package ch.sbb.atlas.workflow.termination;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

class TerminationStopPointFeatureTogglingServiceTest {

  private TerminationStopPointFeatureTogglingService terminationStopPointFeatureTogglingService;

  @BeforeEach
  void initMocksAndService() {
    MockitoAnnotations.openMocks(this);
    terminationStopPointFeatureTogglingService = new TerminationStopPointFeatureTogglingService();
  }

  @Test
  void shouldThrowUnsupportedExceptionWhenFeatureNotEnabled() {
    //given
    terminationStopPointFeatureTogglingService.setTerminationWorkflowEnabled(false);
    //when and then
    assertThrows(UnsupportedOperationException.class, () -> terminationStopPointFeatureTogglingService.checkIsFeatureEnabled());
  }

  @Test
  void shouldNotThrowUnsupportedExceptionWhenFeatureIsEnabled() {
    //given
    terminationStopPointFeatureTogglingService.setTerminationWorkflowEnabled(true);
    //when and then
    assertDoesNotThrow(() -> terminationStopPointFeatureTogglingService.checkIsFeatureEnabled());
  }

}