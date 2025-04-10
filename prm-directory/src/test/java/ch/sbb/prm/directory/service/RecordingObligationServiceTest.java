package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.repository.RecordingObligationRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class RecordingObligationServiceTest {

  private static final String SLOID = "ch:1:sloid:12345";

  @Autowired
  private RecordingObligationRepository recordingObligationRepository;

  @Autowired
  private RecordingObligationService recordingObligationService;

  @AfterEach
  void tearDown() {
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