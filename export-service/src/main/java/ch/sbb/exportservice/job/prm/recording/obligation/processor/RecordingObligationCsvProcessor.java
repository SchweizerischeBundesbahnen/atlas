package ch.sbb.exportservice.job.prm.recording.obligation.processor;

import ch.sbb.exportservice.job.prm.recording.obligation.entity.RecordingObligation;
import ch.sbb.exportservice.job.prm.recording.obligation.model.RecordingObligationCsvModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class RecordingObligationCsvProcessor implements ItemProcessor<RecordingObligation, RecordingObligationCsvModel> {

  @Override
  public RecordingObligationCsvModel process(RecordingObligation recordingObligation) {
    return RecordingObligationCsvModel.builder()
        .sloid(recordingObligation.getSloid())
        .recordingObligation(recordingObligation.isRecordingObligation())
        .build();
  }

}
