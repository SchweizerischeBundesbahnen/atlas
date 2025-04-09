package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.repository.RecordingObligationRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class RecordingObligationServiceTest {

  private static final String SLOID = "ch:1:sloid:12345";

  @Autowired
  private StopPointRepository stopPointRepository;

  @Autowired
  private RecordingObligationRepository recordingObligationRepository;

  @Autowired
  private RecordingObligationService recordingObligationService;

  @BeforeEach
  void setUp() {
    stopPointRepository.save(StopPointTestData.getStopPointVersion());
  }

  @AfterEach
  void tearDown() {
    stopPointRepository.deleteAll();
    recordingObligationRepository.deleteAll();
  }

  @Test
  void shouldBeRecordingObligationOnDefault() {
    boolean recordingObligation = recordingObligationService.getRecordingObligation(SLOID);
    assertThat(recordingObligation).isTrue();
  }

  @Test
  void shouldSaveRecordingObligation() {
    recordingObligationService.setRecordingObligation(SLOID, false);

    boolean recordingObligation = recordingObligationService.getRecordingObligation(SLOID);
    assertThat(recordingObligation).isFalse();
  }

  @Test
  void shouldGetRecordingObligations() {
    recordingObligationService.setRecordingObligation(SLOID, false);

    Map<String, Boolean> recordingObligations = recordingObligationService.getRecordingObligations(List.of(SLOID));
    assertThat(recordingObligations.get(SLOID)).isFalse();
  }
}