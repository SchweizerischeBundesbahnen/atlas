package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.prm.model.stoppoint.RecordingObligationModel;
import ch.sbb.prm.directory.api.StopPointApiInternal;
import ch.sbb.prm.directory.service.RecordingObligationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StopPointApiInternalController implements StopPointApiInternal {

  private final RecordingObligationService recordingObligationService;

  @Override
  public void updateRecordingObligation(String sloid, RecordingObligationModel recordingObligation) {
    recordingObligationService.setRecordingObligation(sloid, recordingObligation.getValue());
  }

  @Override
  public RecordingObligationModel getRecordingObligation(String sloid) {
    return RecordingObligationModel.builder()
        .value(recordingObligationService.getRecordingObligation(sloid))
        .build();
  }

}
