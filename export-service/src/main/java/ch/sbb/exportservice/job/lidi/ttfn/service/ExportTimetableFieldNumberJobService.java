package ch.sbb.exportservice.job.lidi.ttfn.service;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_TTFN_CSV_JOB_NAME;
import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_TTFN_JSON_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportTimetableFieldNumberJobService extends BaseExportJobService {

  public ExportTimetableFieldNumberJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_TTFN_CSV_JOB_NAME) Job exportLoadingPointCsvJob,
      @Qualifier(EXPORT_TTFN_JSON_JOB_NAME) Job exportLoadingPointJsonJob
  ) {
    super(jobLauncher, exportLoadingPointCsvJob, exportLoadingPointJsonJob);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return List.of(
        new JobParams(ExportTypeV2.FULL),
        new JobParams(ExportTypeV2.ACTUAL),
        new JobParams(ExportTypeV2.FUTURE_TIMETABLE)
    );
  }

  @Override
  public ExportObjectV2 getExportObject() {
    return ExportObjectV2.TIMETABLE_FIELD_NUMBER;
  }

}
