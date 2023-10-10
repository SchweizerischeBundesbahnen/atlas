package ch.sbb.prm.directory.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.exception.StopPlaceDoesNotExistsException;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StopPlaceVersionServiceTest {

  @Mock
  private StopPlaceRepository stopPlaceRepository;
  private StopPlaceService stopPlaceService;

  private VersionableService versionableService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    stopPlaceService = new StopPlaceService(stopPlaceRepository, versionableService);
  }

  @Test
  void shouldThrowsExceptionWhenStopPlaceDoesNotExists(){
    assertThrows(StopPlaceDoesNotExistsException.class,
        () -> stopPlaceService.checkStopPlaceExists("ch:1:sloid:70000")).getLocalizedMessage();
  }

  @Test
  void shouldNotThrowsExceptionWhenStopPlaceExists(){
    //given
    doReturn(true).when(stopPlaceRepository).existsBySloid("ch:1:sloid:70000");
    //when && then
    assertDoesNotThrow(() ->  stopPlaceService.checkStopPlaceExists("ch:1:sloid:70000"));
  }


}
