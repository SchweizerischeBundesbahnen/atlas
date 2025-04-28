package ch.sbb.exportservice.job.prm.recording.obligation.service;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_RECORDING_OBLIGATION_CSV_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RecordingObligationJobService extends BaseExportJobService {

  public RecordingObligationJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_RECORDING_OBLIGATION_CSV_JOB_NAME) Job exportRecordingObligationCsvJob) {
    super(jobLauncher, exportRecordingObligationCsvJob, null);
  }

  @Override
  public void startExportJobs() {
    log.info("CSV export execution started...");
    for (JobParams jobParams : getExportTypes()) {
      startExportJob(jobParams, getExportCsvJob());
    }
    log.info("CSV export execution finished");
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return List.of(new JobParams(ExportTypeV2.FULL));
  }

  @Override
  public ExportObjectV2 getExportObject() {
    return ExportObjectV2.RECORDING_OBLIGATION;
  }

}
