package ch.sbb.prm.directory.service.versioning;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistsException;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.service.SharedServicePointService;
import ch.sbb.prm.directory.service.StopPointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StopPointVersionServiceTest {

  @Mock
  private StopPointRepository stopPointRepository;
  private StopPointService stopPointService;

  private VersionableService versionableService;

  private SharedServicePointService sharedServicePointService;

  StopPointVersionServiceTest() {
  }

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    stopPointService = new StopPointService(stopPointRepository, versionableService, sharedServicePointService);
  }

  @Test
  void shouldThrowsExceptionWhenStopPointDoesNotExists(){
    assertThrows(StopPointDoesNotExistsException.class,
        () -> stopPointService.checkStopPointExists("ch:1:sloid:70000")).getLocalizedMessage();
  }

  @Test
  void shouldNotThrowsExceptionWhenStopPointExists(){
    //given
    doReturn(true).when(stopPointRepository).existsBySloid("ch:1:sloid:70000");
    //when && then
    assertDoesNotThrow(() ->  stopPointService.checkStopPointExists("ch:1:sloid:70000"));
  }


}
