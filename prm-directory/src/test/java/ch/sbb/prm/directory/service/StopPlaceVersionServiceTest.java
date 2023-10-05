package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import ch.sbb.prm.directory.repository.StopPlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StopPlaceVersionServiceTest {

  @Mock
  private StopPlaceRepository stopPlaceRepository;
  private StopPlaceService stopPlaceService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    stopPlaceService = new StopPlaceService(stopPlaceRepository);
  }

  @Test
  void shouldThrowsExceptionWhenStopPlaceDoesNotExists(){
    String message = assertThrows(IllegalStateException.class,
        () -> stopPlaceService.checkStopPlaceExists("ch:1:sloid:70000")).getLocalizedMessage();
    assertThat(message).isEqualTo("StopPlace with sloid [ch:1:sloid:70000] does not exists!");
  }

  @Test
  void shouldNotThrowsExceptionWhenStopPlaceExists(){
    //given
    doReturn(true).when(stopPlaceRepository).existsBySloid("ch:1:sloid:70000");
    //when && then
    assertDoesNotThrow(() ->  stopPlaceService.checkStopPlaceExists("ch:1:sloid:70000"));
  }


}
