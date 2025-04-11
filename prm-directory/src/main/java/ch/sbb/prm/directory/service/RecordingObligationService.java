package ch.sbb.prm.directory.service;

import ch.sbb.prm.directory.entity.RecordingObligation;
import ch.sbb.prm.directory.repository.RecordingObligationRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecordingObligationService {

  private final RecordingObligationRepository recordingObligationRepository;

  @PreAuthorize("@prmUserAdministrationService.isAtLeastPrmSupervisor()")
  public void setRecordingObligation(String sloid, boolean value) {
    Optional<RecordingObligation> existingObligation = recordingObligationRepository.findById(sloid);
    if (existingObligation.isPresent()) {
      existingObligation.get().setRecordingObligation(value);
      recordingObligationRepository.save(existingObligation.get());
    } else {
      recordingObligationRepository.save(RecordingObligation.builder()
          .sloid(sloid)
          .recordingObligation(value)
          .build());
    }
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
