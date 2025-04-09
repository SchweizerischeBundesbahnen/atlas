package ch.sbb.prm.directory.service;

import ch.sbb.prm.directory.entity.RecordingObligation;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistException;
import ch.sbb.prm.directory.repository.RecordingObligationRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecordingObligationService {

  private final RecordingObligationRepository recordingObligationRepository;
  private final StopPointRepository stopPointRepository;

  public void setRecordingObligation(String sloid, boolean value) {
    if (!stopPointRepository.existsBySloid(sloid)) {
      throw new StopPointDoesNotExistException(sloid);
    }
    recordingObligationRepository.save(RecordingObligation.builder()
        .sloid(sloid)
        .recordingObligation(value)
        .build());
  }

  public boolean getRecordingObligation(String sloid) {
    Optional<RecordingObligation> savedRecordingObligation = recordingObligationRepository.findById(sloid);
    return savedRecordingObligation.map(RecordingObligation::isRecordingObligation).orElse(true);
  }

  public Map<String, Boolean> getRecordingObligations(List<String> sloids) {
    List<RecordingObligation> recordingObligations = recordingObligationRepository.findAllBySloidIn(sloids);
    return recordingObligations.stream()
        .collect(Collectors.toMap(RecordingObligation::getSloid, RecordingObligation::isRecordingObligation));
  }

}
