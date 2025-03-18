package ch.sbb.exportservice.job.trafficpoint;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.SePoDiExportType;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportTrafficPointElementJobService extends BaseExportJobService {

  public ExportTrafficPointElementJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME) Job exportTrafficPointElementCsvJob,
      @Qualifier(EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME) Job exportTrafficPointElementJsonJob) {
    super(jobLauncher, exportTrafficPointElementCsvJob, exportTrafficPointElementJsonJob);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return List.of(
        new JobParams(ExportTypeV2.WORLD_FULL, SePoDiExportType.WORLD_FULL),
        new JobParams(ExportTypeV2.WORLD_ACTUAL, SePoDiExportType.WORLD_ONLY_ACTUAL),
        new JobParams(ExportTypeV2.WORLD_FUTURE_TIMETABLE, SePoDiExportType.WORLD_ONLY_TIMETABLE_FUTURE)
    );
  }

}
