package ch.sbb.exportservice.job.prm.recording.obligation.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.BaseCsvWriter;
import ch.sbb.exportservice.job.prm.recording.obligation.model.RecordingObligationCsvModel;
import ch.sbb.exportservice.job.prm.recording.obligation.model.RecordingObligationCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvRecordingObligationCsvModelWriter extends BaseCsvWriter<RecordingObligationCsvModel> {

  CsvRecordingObligationCsvModelWriter(FileService fileService) {
    super(fileService);
  }

  @Override
  protected String[] getCsvHeader() {
    return new String[]{
        RecordingObligationCsvModel.Fields.sloid, Fields.recordingObligation,
    };
  }

}
