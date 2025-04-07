package ch.sbb.exportservice.job.prm.relation.service;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_RELATION_CSV_JOB_NAME;
import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_RELATION_JSON_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.PrmExportType;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportRelationJobService extends BaseExportJobService {

  public ExportRelationJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_RELATION_CSV_JOB_NAME) Job exportRelationsCsvJob,
      @Qualifier(EXPORT_RELATION_JSON_JOB_NAME) Job exportRelationsJsonJob) {
    super(jobLauncher, exportRelationsCsvJob, exportRelationsJsonJob);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return List.of(
        new JobParams(ExportTypeV2.FULL, PrmExportType.FULL),
        new JobParams(ExportTypeV2.ACTUAL, PrmExportType.ACTUAL),
        new JobParams(ExportTypeV2.FUTURE_TIMETABLE, PrmExportType.TIMETABLE_FUTURE)
    );
  }

  @Override
  public ExportObjectV2 getExportObject() {
    return ExportObjectV2.RELATION;
  }

}
