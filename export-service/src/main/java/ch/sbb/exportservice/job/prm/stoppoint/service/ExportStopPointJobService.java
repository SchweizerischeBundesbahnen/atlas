package ch.sbb.exportservice.job.prm.stoppoint.service;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_STOP_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_STOP_POINT_JSON_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.job.BaseExportType;
import ch.sbb.exportservice.model.ExportObjectV2;
import java.util.List;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportStopPointJobService extends BaseExportJobService {

  public ExportStopPointJobService(
      JobOperator jobOperator,
      @Qualifier(EXPORT_STOP_POINT_CSV_JOB_NAME) Job exportStopPointCsvJob,
      @Qualifier(EXPORT_STOP_POINT_JSON_JOB_NAME) Job exportStopPointJsonJob) {
    super(jobOperator, exportStopPointCsvJob, exportStopPointJsonJob);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return BaseExportType.BASE;
  }

  @Override
  public ExportObjectV2 getExportObject() {
    return ExportObjectV2.STOP_POINT;
  }

}
