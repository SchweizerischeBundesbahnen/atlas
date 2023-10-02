package ch.sbb.prm.directory.service;

import ch.sbb.prm.directory.repository.StopPlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StopPlaceVersionServiceTest {

  @Mock
  private StopPlaceRepository lineRepository;
  private StopPlaceService stopPlaceService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    stopPlaceService = new StopPlaceService(lineRepository);
  }

}
